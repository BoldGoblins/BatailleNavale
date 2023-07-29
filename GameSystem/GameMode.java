package GameSystem;

import java.lang.NumberFormatException;
import java.util.ArrayList;
import Actors.Ship;
import Enums.EPlayer;

public class GameMode 
{
    public static ArrayList <Ship> initPlayerState(EPlayer player)
    {
        ArrayList <Ship> temp = new ArrayList <Ship> (5);

        if (player == EPlayer.Player1)
        {
            temp.add(new Ship("Le téméraire", 5));
            temp.add(new Ship("Le robuste", 4));
            temp.add(new Ship("Le gracieux", 3));
            temp.add(new Ship("Le fourbe", 3));
            temp.add(new Ship("L'audacieux", 2));
        }
        else
        {
            temp.add(new Ship("Pyongyong", 5));
            temp.add(new Ship("Maki-Sashimi", 4));
            temp.add(new Ship("Bond-our", 3));
            temp.add(new Ship("Ling-Ying-Ding-Dong", 3));
            temp.add(new Ship("Nhem", 2));
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
