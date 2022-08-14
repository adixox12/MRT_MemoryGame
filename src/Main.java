import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.lang.Thread;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class Main {

    public static String filename = "Words.txt";
    public static List<String> words;
    public static List<String> randomWords = new ArrayList<>();
    public static int difficultyLevel = 0;
    public static int guessChances = 0;
    public static int amountOfMemoryCards = 0;
    public static char[] alphabetLetters = new char[]{'A','B','C','D'};

    public static int amountOfChancesForEasy = 10;
    public static int amountOfChancesForHard = 15;
    //public static String[][] scoreTable = new String[10][4];
    public static List<Score> scoreTable = new ArrayList<>();
    public static int lastTime=0;
    public static int lastGuessChances = 0;
    public static String filenameLeaderboards = "none.txt";



    public static void main(String[] args) throws InterruptedException{

        while (true)
        {
            words = LoadWords(filename);
            ShowDifficultySelectionScreen();
            SelectRandomWords();
            GameLoop();
            if (!EndScreen()) break;
        }

        System.out.println("Leaving application...");
    }

    private static boolean EndScreen()
    {
        int selection = 0;

        while (selection < 1 || selection > 3 )
        {
            selection = ScannerNextInt();
        }

        if (selection == 1)
        {
            difficultyLevel = 0;
            randomWords = new ArrayList<>();
            return true;
        }
        else if (selection == 2)
        {
            if (lastTime == 0)
            {
                LoadScores();
                ViewLeaderboards();
            }
            else
            {
                SaveScore();
            }
            return true;
        }
        else
        {
            System.exit(0);
            return false;
        }
    }

    private static void GameLoop() throws InterruptedException
    {
        boolean[] guessedMemoryCards = new boolean[amountOfMemoryCards*2];
        int[] selectedCards = new int[]{-1,-1};
        long gameStartTime = System.currentTimeMillis();

        while (true){
            GameHUD.PrintGameHUD_top(difficultyLevel,guessChances);
            for (int i = 0; i < difficultyLevel*2; i++) {
                String textQuery = "";
                for (int j = 0; j < 4; j++)
                {
                    int currentCardByID = i*4+j;

                    boolean selectCard = selectedCards[0] == currentCardByID || selectedCards[1] == currentCardByID;

                    textQuery += " ";
                    if (selectCard) textQuery += ">";
                    if (guessedMemoryCards[currentCardByID] || selectCard) textQuery += randomWords.get(i*4+j);
                    else textQuery += " X ";
                    if (selectCard) textQuery += "<";
                    textQuery += " ";
                }
                System.out.println(alphabetLetters[i]+"  "+textQuery);
            }
            GameHUD.GenerateDecorationLine();
            Scanner scanner = new Scanner(System.in);
            String selectedCard;

            if (selectedCards[1] != -1)
            {
                if (randomWords.get(selectedCards[0]) == randomWords.get(selectedCards[1]))
                {
                    System.out.println("You managed to discover a couple!");
                    guessedMemoryCards[selectedCards[0]] = true;
                    guessedMemoryCards[selectedCards[1]] = true;

                    for (int i = 0; i < amountOfMemoryCards*2; i++)
                    {
                        if(!guessedMemoryCards[i]) break;
                        if (i == amountOfMemoryCards*2-1)
                        {
                            long gameFinishTime = System.currentTimeMillis();
                            long timeElapsed = gameFinishTime - gameStartTime;
                            lastTime = (int)(timeElapsed/1000);
                            lastGuessChances = 10+(difficultyLevel-1)*5-guessChances;
                            GameHUD.PrintEndGameScreen(true,lastGuessChances, lastTime);
                            return;
                        }
                    }
                }
                else
                {
                    System.out.println("The cards are not the same.");
                    guessChances--;
                }

                Thread.sleep(1500);
                selectedCards[0] = -1;
                selectedCards[1] = -1;
            }
            else
            {
                while (true)
                {
                    System.out.println("Select a card. For example: B3");
                    selectedCard = scanner.nextLine().toUpperCase(Locale.ROOT);
                    int _id = GetCardIDByGameCords(selectedCard);

                    if (_id == -2) System.out.println("Please enter valid input.");
                    else
                    {
                        if (!guessedMemoryCards[_id])
                        {
                            if (selectedCards[0] != -1 && selectedCards[0] == _id) System.out.println("You can't uncover the same card twice in one move.");
                            else break;
                        }
                        else System.out.println("This card is already uncovered!");
                    }
                }

                if (selectedCards[0] == -1) selectedCards[0]=GetCardIDByGameCords(selectedCard);
                else selectedCards[1]=GetCardIDByGameCords(selectedCard);
            }

            if (guessChances <= 0)
            {
                long gameFinishTime = System.currentTimeMillis();
                long timeElapsed = gameFinishTime - gameStartTime;
                GameHUD.PrintEndGameScreen(false,10+(difficultyLevel-1)*5-guessChances, (int)(timeElapsed/1000));
                lastGuessChances=0;
                lastTime=0;
                break;
            }
        }
    }

    private static void SelectRandomWords()
    {
        while ((long) randomWords.size() <amountOfMemoryCards)
        {
            String word = words.get((int) (Math.random() * (long) words.size()));
            randomWords.add(word);
            List<String> tempList = randomWords.stream().distinct().collect(Collectors.toList());
            randomWords = tempList;
        }

        for (int i = 0; i < amountOfMemoryCards; i++) {
            randomWords.add(randomWords.get(i));
        }

        Collections.shuffle(randomWords);
    }

    private static void ShowDifficultySelectionScreen()
    {
        while (difficultyLevel < 1 || difficultyLevel > 2 )
        {
            GameHUD.PrintSelectDifficultyLevel();
            difficultyLevel = ScannerNextInt();

            if (difficultyLevel == 1)
            {
                guessChances = amountOfChancesForEasy;
                filenameLeaderboards = "HighscoreTable.txt";
            }
            else
            {
                guessChances = amountOfChancesForHard;
                filenameLeaderboards = "HighscoreTableHard.txt";
            }

            amountOfMemoryCards = 4*difficultyLevel;
        }
    }

    private static int ScannerNextInt()
    {
        while (true)
        {
            try {
                Scanner scanner = new Scanner(System.in);
                return scanner.nextInt();
            }
            catch (final Exception e) {
                System.out.println("Please enter valid input.");
            }
        }


    }

    private static int GetCardIDByGameCords(String gameCords){

        try {
            int result = 0;
            if (gameCords.charAt(0) == 'B') result = 4;
            else if (gameCords.charAt(0) == 'C') result = 8;
            else if (gameCords.charAt(0) == 'D') result = 12;
            result += Integer.parseInt(String.valueOf(gameCords.charAt(1)))-1;

            if (result < 0 || result >= amountOfMemoryCards*2) result = -2;
            return result;
        }
        catch (final Exception e) {
            return -2;
        }

    }
    private static List<String> LoadWords(String filename) {
        List<String> result;

        try {
            result = Files.readAllLines(new File(filename).toPath(), Charset.defaultCharset());
            return result;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("File "+filename+" not found!");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.exit(0);
            return null;
        }
    }

    private static void SaveScore()
    {
        GameHUD.PrintEnterYourNameScreen();
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        LoadScores();
        scoreTable.add(new Score(name,dtf.format(now),lastTime,lastGuessChances));
        SortScores();
        SaveScores();
        ViewLeaderboards();
    }

    private static void SortScores()
    {
        for (int j = 0; j < 10; j++)
        {
            for (int i = 0; i < 10; i++)
            {
                try {
                    if (scoreTable.get(i).time > scoreTable.get(i + 1).time) {
                        Collections.swap(scoreTable, i, i + 1);
                    }
                } catch (final Exception e)
                {
                    break;
                }
            }
        }

        if ((long) scoreTable.size() > 10) scoreTable.remove(10);
    }

    private static void LoadScores()
    {
        List<String> tempList = LoadWords(filenameLeaderboards);
        scoreTable = new ArrayList<>();

        for (int i = 0; i < tempList.size()/4; i++)
        {
          scoreTable.add(new Score(tempList.get(i*4),tempList.get(i*4+1),Integer.parseInt(tempList.get(i*4+2)),Integer.parseInt(tempList.get(i*4+3))));
        }
    }

    private static void SaveScores()
    {
        List<String> tempList = new ArrayList<>();
        for (Score score : scoreTable) {
            tempList.add(score.name);
            tempList.add(score.date);
            tempList.add(String.valueOf(score.time));
            tempList.add(String.valueOf(score.guessTries));
        }

        try {
            Files.write(new File(filenameLeaderboards).toPath(), tempList,Charset.defaultCharset());
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error while saving has occurred.");
        }

    }

    private static void ViewLeaderboards()
    {
        GameHUD.GenerateDecorationLine();

        for (int i = 0; i < scoreTable.size(); i++)
        {
            System.out.println(i+1+". "+scoreTable.get(i).name+" "+
                    scoreTable.get(i).date+" | time: "+
                    scoreTable.get(i).time +" | ch: "+
                    scoreTable.get(i).guessTries);
        }

        GameHUD.GenerateDecorationLine();
        System.out.println("1: Play again    2: Exit game");

        int selection = 0;
        while (selection < 1 || selection > 2 )
        {
            selection = ScannerNextInt();
        }

        if (selection == 1)
        {
            difficultyLevel = 0;
            randomWords = new ArrayList<>();
        }
        else
        {
            System.exit(0);
        }
    }

}