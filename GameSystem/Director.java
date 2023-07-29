package GameSystem;
import BoldGoblins.Exceptions.*;
import BoldGoblins.Utilitaires.BGRandomiser;
import BoldGoblins.Utilitaires.BGYesNoQReturn;
import Enums.ECellState;
import Enums.EPlayer;
import Scenes.Scene;
import Actors.Player;
import Actors.Ship;
import Actors.Cell;
import GameSystem.PlayerController;
import GameSystem.GameMode;
import GameSystem.GameState;
import GameSystem.AI_Controller;
import Scenes.Map;

public class Director 
{
    public static void placePlayerShips(GameState Gs, Map map)
    {
        renderMap(map, EPlayer.Player2);

        for (Ship sh : Gs.getPlayer(EPlayer.Player1).getFleet())
        {
            System.out.println("Placer le navire : " + sh.getName());
               
            while (true)
            {
                StringBuilder input = new StringBuilder();

                if (!PlayerController.cellSelectionInput(input, "Selectionner une case : "))
                    continue;

                if (!(Gs.tryPlaceShipAt(EPlayer.Player1, sh, GameMode.convertInputToIndex(input.toString()))))
                {
                    System.out.println("Impossible de placer le navire en " + input + '.');
                    continue;
                }

                renderMap(map, EPlayer.Player1);

                while (true)
                {
                    BGYesNoQReturn choice = PlayerController.yesOrNoInput("Effectuer une rotation ? (y/n)");

                    if (choice.mbValidAnswer)
                    {
                        if (choice.mbYes)
                        {
                            Gs.tryRotateShipAt(sh, GameMode.convertInputToIndex(input.toString()));
                            renderMap(map, EPlayer.Player1);
                        }
                        else
                            break;
                    }
                }
                
                boolean bExitLoop = false;

                while (true)
                {
                    BGYesNoQReturn choice = PlayerController.yesOrNoInput("Confirmer emplacement du navire ? (y/n)");

                    if (choice.mbValidAnswer)
                    {
                        bExitLoop = choice.mbYes;
                        break;
                    }
                }

                if (bExitLoop)
                    break;
    
            }
        }
    }

    // Map en paramètre juste pour le DEBUG
    public static void placeAIShips(GameState Gs, AI_Controller aic, Map map)
    {
        Gs.flipCurrentPlayer();

        try
        {
            aic.placeShips(Gs);
        }
        catch(AI_ControllerExcept exp)
        {
            System.err.println(exp.getMessage());
        }
    }

    public static void gameLoop(GameState Gs, AI_Controller aic, Map map)
    {
       // PlayerController.screenClearer();
        Gs.setFirstPlayer(BGRandomiser.getRandomBool());

        do
        {
            boolean bHit = false;
            
            fleetStateDisplay(Gs);

            if (GameState.getCurrentPlayer() == EPlayer.Player2)
            {
                int hitIndex = aic.playMove(Gs);
                int adverseFleetCount = Gs.getPlayer(EPlayer.Player1).getFleetCount();

                bHit = shootCell(Gs, EPlayer.Player2, EPlayer.Player1, hitIndex);

                aic.lastHitInfos(hitIndex, bHit, adverseFleetCount == Gs.getPlayer(EPlayer.Player1).getFleetCount());

                renderMap(map, EPlayer.Player1);
            }         
            else
            {
                renderMap(map, EPlayer.Player2);

                while(true)
                {
                    StringBuilder input = new StringBuilder();  

                    if (!PlayerController.cellSelectionInput(input, "Choisir une case où frapper : "))
                        continue;

                    int index = GameMode.convertInputToIndex(input.toString());

                    if (GameState.getPlayerMap(EPlayer.Player2).get(index).getCellState() == ECellState.Bombed)
                    {
                        System.out.println("Case déjà bombardée. Choisir une autre case.");
                        continue;
                    }

                    bHit = shootCell(Gs, EPlayer.Player1, EPlayer.Player2, index);
                    renderMap(map, EPlayer.Player2);
                    break;                  
                }
            }

            if (!bHit)
                Gs.flipCurrentPlayer();

        } while (!Gs.getPlayer(EPlayer.Player1).hasLoose() && !Gs.getPlayer(EPlayer.Player2).hasLoose());

        // CurrentPlayer -> celui qui jouait lorsque l'autre joueur a perdu, c'est donc le gagnant.
        String winnerName = Gs.getPlayer(GameState.getCurrentPlayer()).mName;

        System.out.println("Victoire de : " + winnerName + " !");
    }

    // Gère Cell.setBombed(), Player.shoot(), Ship.hit()
    // Display case frappé et ship touché ou détruit pour les deux Player.
    // Return boolean bHit pour flow control de la gameLoop()
    // Il faut juste traiter le cas de la case déjà bombardée en dehors de cette fonction.
    private static boolean shootCell(GameState gs, EPlayer player, EPlayer target, int index)
    {
        Cell cell = GameState.getPlayerMap(target).get(index);
        boolean bHit = false;

        String name = gs.getPlayer(player).mName;

        // Séparer la fonction qui génère la String du println sinon ça bug.
        String idDisplay = GameMode.convertIndexToDisplay(index);
        System.out.println(name + " a frappé en " + idDisplay);

        if (cell.getCellState() == ECellState.Filled)
            bHit = true;

        if(bHit)
        {
            Ship ship = gs.getPlayer(target).getShip(cell.getShipHash());

            ship.hit();

            if (ship.isAlive())
                System.out.println(name + " a touché le navire : " + ship.getName());
            else
            {
                gs.getPlayer(target).shipSunk(ship.getHashCode());
                System.out.println(name + " a détruit le navire : " + ship.getName());
            }
        }
        else
            System.out.println(name + " a manqué sa cible.");


        cell.setBombed();
        gs.getPlayer(player).shoot(bHit);

        return bHit;
    }
    private static void fleetStateDisplay (GameState gs)
    {
        EPlayer player = EPlayer.Player1;

        // On veut display les infos du joueur adverse à celui qui est en train de jouer.
        if (GameState.getCurrentPlayer() == EPlayer.Player1)
            player = EPlayer.Player2;

        int count = gs.getPlayer(player).getFleetCount();
        String name = gs.getPlayer(player).mName;

        System.out.println("Il reste à " + name + " " + count + " bateau(x) en état de combattre.");
    }

    private static void renderMap(Map map, EPlayer player)
    {
        try 
        {
            map.show(player);
        }
        catch (UninitializedMap Unmap)
        {
            System.err.print(Unmap.getMessage());
        }
    }

    public static void renderScene(Scene scene)
    {

    }
}