package Scenes;

import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;

import BoldGoblins.Utilitaires.BGTextFilesReader;
import Enums.ECellState;
import Enums.EPlayer;
import GameSystem.GameState;
import BoldGoblins.Exceptions.UninitializedMap;
import Actors.Cell;

public class Map
{
    
    // charge la carte en mémoire depuis un fichier texte
    public Map(String mapName)
    {
        StringBuilder str = new StringBuilder("Maps\\");

        str.append(mapName).append(".txt");
        
        BGTextFilesReader.loadTextFile(str.toString(), m_Map);
    }

    // Throws UninitializedMap
    public void show(EPlayer player)
    {
        if (m_Map.isEmpty())
            throw new UninitializedMap();

        System.out.println(m_Map.get(0).toString());

        for (int i = 0; i < 10; ++i)
        {
            System.out.println(m_Map.get((i * 3) + 1));

            try
            {
                StringBuilder str1 = new StringBuilder (m_Map.get(1 + (i * 3) + 1).toString());
                StringBuilder str2 = new StringBuilder (m_Map.get(1 + (i * 3) + 2).toString());

                for(int j = 0; j < 10; ++j)
                {
                    Cell cell = GameState.getPlayerMap(player).get(i * 10 + j);

                    if (cell.getCellState() == ECellState.Bombed || cell.getCellState()  == ECellState.Filled)
                    {
                        String toReplace = "    ";
                        
                        if (cell.getCellState()  == ECellState.Bombed)
                        {
                            if (cell.getShipDestroyed())
                                toReplace = this.ShipDestroyed;
                            else
                                toReplace = this.Bombed;
                        }
                        else if (cell.getCellState()  == ECellState.Filled && player == EPlayer.Player1)
                            toReplace = this.Filled;

                        str1.replace(2 + j * 5, 2 + (j * 5) + 4, toReplace);
                        str2.replace(2 + j * 5, 2 + (j * 5) + 4, toReplace);
                    }
                }

                System.out.println(str1.toString());
                System.out.println(str2.toString());
            }
            catch (IndexOutOfBoundsException e)
            {
                System.err.println("Exception IndexOutOfBoundsException in Map.display().");
                return;
            }
        }

        System.out.println(m_Map.get(31).toString());
    }

    private ArrayList <StringBuilder> m_Map = new ArrayList <StringBuilder> (1 + (2 * 10) + 11);
    private String Filled = "****";
    private String Bombed = "0000";
    private String ShipDestroyed = "xxxx";
}
