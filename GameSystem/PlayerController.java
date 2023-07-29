package GameSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.NumberFormatException;

import BoldGoblins.Exceptions.PlayerControllerExcept;
import BoldGoblins.Utilitaires.BGYesNoQReturn;
import Enums.ECellState;
import Actors.Cell;
import Actors.Player;

public class PlayerController
{
    public static void screenClearer()
    {
        for(int i = 0; i < 30; ++i)
            System.out.println(" ");
    }

    
    public static BGYesNoQReturn yesOrNoInput(String question)
    {
        BGYesNoQReturn toRet = new BGYesNoQReturn();

        System.out.println(question);
        try
        {
            toRet.mbYes = parseYesNoInput();
            toRet.mbValidAnswer = true;
        }
        catch(PlayerControllerExcept exp)
        {
            System.out.println("Erreur d'entrée utilisateur. Recommencer.");
            toRet.mbValidAnswer = false;

            return toRet;
        }

        return toRet;
    }

    public static boolean cellSelectionInput(StringBuilder input, String question)
    {
        input.delete(0, input.length());

        System.out.println(question);

        try
        {
            input.append(PlayerController.parseCellInput());
            return true;
        }
        catch (PlayerControllerExcept exp)
        {
            System.out.println("Entrée invalide, veuillez saisir une lettre [a-j] suivie d'un nombre [1-10].");
            return false;
        }
    }

    // Throw PlayerControllerExcept
    // Return "null" if IOException (stream fermé)
    private static boolean parseYesNoInput()
    {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));

        try
        {
            String str = bfr.readLine();

            if (str.length() > 1 || str.isEmpty())
                throw new PlayerControllerExcept("Error in confirmChoice : InvalidInput");

            if (str.charAt(0) == 'y' || str.charAt(0) == 'Y')
                return true;

            else if (str.charAt(0) == 'n' || str.charAt(0) == 'N')
                return false;

            else 
                throw new PlayerControllerExcept("Error in confirmChoice : InvalidInput");
        }
        catch(IOException exp)
        {
            // Possible de logger ici car flux fermé
            return false;
        }
    }

    // Throw PlayerControllerExcept
    // Return "null" if IOException (stream fermé)
    private static String parseCellInput()
    {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));

        StringBuilder str = new StringBuilder(2);

        int letter = 0;
        int number = -1;

        try
        {
            letter = bfr.read();
            number = Integer.parseInt(bfr.readLine());
        }
        catch (IOException exp)
        {
            // Possible de logger ici car flux fermé
            System.exit(0);
        }
        catch(NumberFormatException exp)
        {
            number = - 1;
            throw new PlayerControllerExcept("Error in cellSelection : Invalid Input.");
        }
        
        if((letter >= 65 && letter <= 74) || (letter >= 97 && letter <= 106))
            str.append((char) letter);
        else
            throw new PlayerControllerExcept("Error in cellSelection : Invalid Input.");

        if(number >= 1 && number <= 10)
            str.append(number);
        else    
            throw new PlayerControllerExcept("Error in cellSelection : Invalid Input.");

        return str.toString();
    }
}
