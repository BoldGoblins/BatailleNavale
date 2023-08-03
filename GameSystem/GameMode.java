package GameSystem;

import java.lang.NumberFormatException;
import java.util.ArrayList;

import Actors.Ship;
import Enums.EPlayer;
import BoldGoblins.Exceptions.GameModeExcept;

public class GameMode 
{
    public static ArrayList <Ship> initPlayerState(EPlayer player)
    {
        ArrayList <Ship> temp = new ArrayList <Ship> (5);

        try
        {
            if (player == EPlayer.Player1)
            {
                temp.add(new Ship("Porte-avion \"Le téméraire\"", 5));
                temp.add(new Ship("Cuirassé \"Le robuste\"", 4));
                temp.add(new Ship("Frégate \"Le gracieux\"", 3));
                temp.add(new Ship("Sous-marin \"Le fourbe\"", 3));
                temp.add(new Ship("Torpilleur \"L'audacieux\"", 2));
            }
            else
            {
                temp.add(new Ship("Porte-avion \"Pyongyong\"", 5));
                temp.add(new Ship("Cuirassé \"Maki-Sashimi\"", 4));
                temp.add(new Ship("Frégate \"Bond-our\"", 3));
                temp.add(new Ship("Sous-marin \"Ling-Ying-Ding-Dong\"", 3));
                temp.add(new Ship("Torpilleur \"Nhem\"", 2));
            }
        }
        catch(GameModeExcept expt)
        {
            // Tentative d'utiliser un Ship de taille 0.
            System.err.println(expt.getMessage());
            System.exit(-1);
        }

        return temp;
    }

    public static int convertInputToIndex(String s)
    {
        try
        {
            char c = s.charAt(0);
            int n = Integer.parseInt(s.substring(1, s.length()));
            n -= 1;
            int index = 0;

            // majuscule
            if ((int) c - 65 <= 9)
                index += ((int) c - 65);
            else
                index += ((int) c - 97);

            index *= 10;

            return (index + n);
        }
        catch(NumberFormatException exp)
        {
            System.err.println("Error in GameMode convertToMapIndex.");
            return -1;
        }

    }

    public static String convertIndexToDisplay(int index)
    {
        int remainder = index % 10;
        int letter = (index - remainder) / 10;

        letter += 65;

        char c = (char) letter;
        String str = Character.toString(c) + Integer.toString(remainder + 1);
        
        return str;
    }
}
