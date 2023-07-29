import BoldGoblins.Utilitaires.BGYesNoQReturn;
import GameSystem.AI_Controller;
import GameSystem.Director;
import GameSystem.GameState;
import Scenes.Map;

import Debug.GeneralDebugger;
import Enums.EPlayer;

public class MainApp 
{
    public static void main(String[] args)
    {
        GameState GS = new GameState("Napoléon", "Kim-Jung-Un");

        Map map = new Map("Grid01");

        AI_Controller aic = new AI_Controller();

        Director.placePlayerShips(GS, map);

        // GeneralDebugger.autoPlaceShip(EPlayer.Player1, GS);

        Director.placeAIShips(GS, aic, map);

        Director.gameLoop(GS, aic, map);

    }
}

// Polish :
// -> Améliorations/optimisation de certaines fonctions (AI_Controller).
// -> Amélioration de l'IA : prendre en compte la taille des bateaux dans randomSelectionAlongLastPos().
// -> Menu avec possibilité de rejouer.
// -> Ajout de scènes (Victoire/Défaite, sélection personnage ?, couler un navire, ...).
// -> Menu général avec choix de la map et du personnage ?