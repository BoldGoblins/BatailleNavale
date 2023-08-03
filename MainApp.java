import GameSystem.AI_Controller;
import GameSystem.Director;
import GameSystem.GameState;
import Scenes.Map;

public class MainApp 
{
    public static void main(String[] args)
    {
        do
        {
            GameState GS = new GameState("Napoléon", "Kim-Jung-Un");

            Map map = new Map("Grid01");

            AI_Controller aic = new AI_Controller();

            Director.renderScene("image");
            Director.renderScene("title");
            Director.renderScene("logo");

            Director.placePlayerShips(GS, map);

            // GeneralDebugger.autoPlaceShip(EPlayer.Player1, GS);

            Director.placeAIShips(GS, aic);

            Director.gameLoop(GS, aic, map);

        } while (Director.playAgain());

    }
}

// Améliorations :
// -> Placement des Ships par l'IA : stratégie et non plus de l'aléatoire ?
// -> Menu général avec choix de la map et du personnage ?