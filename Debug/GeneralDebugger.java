package Debug;

import Actors.Ship;
import BoldGoblins.Utilitaires.BGRandomiser;
import Enums.EPlayer;
import GameSystem.GameState;

import java.lang.RuntimeException;


public class GeneralDebugger 
{
    public static void autoPlaceShip(EPlayer player, GameState gs)
    {
        for (Ship sh : gs.getPlayer(player).getFleet())
        {
            int limit = 10000;

            while (true)
            {
                --limit;

                if (gs.tryPlaceShipAt(player, sh, BGRandomiser.getRandomInt(100)))
                    break;

                if (limit == 0)
                    throw new RuntimeException("DEBUG MODULE ERROR : autoPlaceShip limit reached.");
            }

        }
    }
}
