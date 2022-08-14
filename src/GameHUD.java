public class GameHUD {

public static void GenerateDecorationLine()
{
    System.out.println("—-----------------------------------");
}

public static void PrintSelectDifficultyLevel()
{
    clearConsole();
    GenerateDecorationLine();
    System.out.println("Select your difficulty level!");
    System.out.println("1. Easy");
    System.out.println("2. Hard\n");
    GenerateDecorationLine();
}

public static void PrintEnterYourNameScreen()
{
    clearConsole();
    GenerateDecorationLine();
    System.out.println("Enter your name!\n");
    GenerateDecorationLine();
}
public static void PrintEndGameScreen(boolean win, int guessChances, int time)
{
    clearConsole();
    GenerateDecorationLine();
    if (win) System.out.println("Congratulations on winning the memory game!");
    else System.out.println("You lost!");
    System.out.println("It took you "+guessChances+" chances and "+time+" seconds.");
    System.out.println("1. Play again");
    if(win) System.out.println("2. Save your time to leaderboards!");
    else System.out.println("2. View leaderboards!");
    System.out.println("3. Exit\n");
    GenerateDecorationLine();
}

public static void PrintGameHUD_top(int diffLevel, int guessChances)
{
    clearConsole();
    String diffLevelText = "easy";
    if (diffLevel == 2) diffLevelText = "hard";
    GenerateDecorationLine();
    System.out.println("Level: "+diffLevelText);
    System.out.println("Guess chances: "+guessChances);
    System.out.println("     1    2    3    4");
}

    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            for(int i = 0; i < 50; i++)
                System.out.print("\n");
        }
    }

}
