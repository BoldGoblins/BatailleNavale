package GameSystem;

import Actors.Ship;
import Actors.Cell;
import Enums.EPlayer;
import Enums.ECellState;
import BoldGoblins.Exceptions.AI_ControllerExcept;
import BoldGoblins.Utilitaires.BGRandomiser;

import java.util.ArrayList;
import java.util.Collections;



public class AI_Controller
{
    public int playMove(GameState gs)
    {
        // Un Ship déjà touché est encore vivant
        if (this.lastContiguousSuccessHit.size() > 0)
        {
            if (this.lastContiguousSuccessHit.size() > 1)
            {
                // toujours > 0 car tableau trié par ordre croissant.
                int dir = this.lastContiguousSuccessHit.get(1) - this.lastContiguousSuccessHit.get(0);

                int frontIndex = this.lastContiguousSuccessHit.get(0) - dir;
                int endIndex = this.lastContiguousSuccessHit.get(this.lastContiguousSuccessHit.size() - 1) + dir;

                boolean bFrontValid = false;
                boolean bEndValid = false;

                if (frontIndex >= 0)
                {
                    if (GameState.getPlayerMap(EPlayer.Player1).get(frontIndex).getCellState() != ECellState.Bombed)
                        bFrontValid = true;                  
                }

                if (endIndex < 100)
                {
                    if (GameState.getPlayerMap(EPlayer.Player1).get(endIndex).getCellState() != ECellState.Bombed)
                        bEndValid = true;
                }

                // pas encore testé un des deux (et les deux sont possibles).
                if (bFrontValid && bEndValid)
                {
                    if (BGRandomiser.getRandomBool())
                        return frontIndex;
                    else
                        return endIndex;
                }

                // déjà testé un des deux (l'autre était Bombed) ou l'un des deux était hors index.
                if (bFrontValid)
                    return frontIndex;
                else
                    return endIndex;
            }
            else
                return randomSelectionAlongLastPos(this.lastContiguousSuccessHit.get(0));
        }

        int indexToHit = queryAdverseMap();

        return indexToHit;
    }

    // Throws AI_ControllerExcept
    public void placeShips(GameState Gs)
    {
        for (Ship sh : Gs.getPlayer(EPlayer.Player2).getFleet())
        {
            int limit = 10000;

            while (true)
            {
                --limit;

                if (Gs.tryPlaceShipAt(EPlayer.Player2, sh, BGRandomiser.getRandomInt(100)))
                    break;

                if (limit == 0)
                    throw new AI_ControllerExcept("placeShips", "limit reached");
            }
        }
    }

    public void lastHitInfos(int index, boolean bSuccess, boolean shipStillAlive)
    {
        if (bSuccess)
        {
            if (shipStillAlive)
            {
                this.lastContiguousSuccessHit.add(index);

                Collections.sort(this.lastContiguousSuccessHit);
            }
            else
                this.lastContiguousSuccessHit.clear();
        }        
    }

    // Ne doit être appelé que si le Ship qui a déjà été touché est encore .alive()
    private int randomSelectionAlongLastPos(int lastSuccessHit)
    {
        ArrayList <Integer> directions = new ArrayList <Integer> (4);

        directions.add(1);
        directions.add(-1);
        directions.add(10);
        directions.add(-10);

        Collections.shuffle(directions);

        for(int dir : directions)
        {
            if (lastSuccessHit + dir < 0 || lastSuccessHit + dir >= 100)
                continue;

            if (lastSuccessHit % 10 == 9 && dir == 1)
                continue;
            
            if (lastSuccessHit % 10 == 0 && dir == -1)
                continue;

            Cell cell = GameState.getPlayerMap(EPlayer.Player1).get(lastSuccessHit + dir);

            // MODIFICATION A FAIRE ICI POUR PRENDRE EN COMPTE LA TAILLE DES BATEAUX.
            if (cell.getCellState() != ECellState.Bombed)
                return lastSuccessHit + dir;
        }

        return 0;
    }

    private int queryAdverseMap()
    {
        // ArrayList <Integer> idTable = new ArrayList <Integer> (10);

        this.targetQueryIndex.clear();

        for(int i = 0; i < 10; ++i)
        { 
            // parse lines : 
            parseMap(i * 10, 1);

            // parse col :
            parseMap(i, 10);
        }

        int idArrTarget = (this.targetQueryIndex.size() - 1) / 2;

        // sécuriser la fonction pour éviter boucle infinie
        while(true)
        {
            if (BGRandomiser.getRandomBool())
                idArrTarget += BGRandomiser.getRandomInt(3);
            else
                idArrTarget -= BGRandomiser.getRandomInt(3);

            if (idArrTarget >= 0 && idArrTarget < this.targetQueryIndex.size())
            {
                if (GameState.getPlayerMap(EPlayer.Player1).get(this.targetQueryIndex.get(idArrTarget)).getCellState() == ECellState.Bombed)
                    continue;
                else
                    break;
            }
        }

        return this.targetQueryIndex.get(idArrTarget);
    }

    // direction : + 1 ou + 10
    private void parseMap(int indexStart, int direction)
    {
        ArrayList <Integer> temp = new ArrayList <Integer> (10);

        int idMax = indexStart + (direction * 10);

        // System.err.println("Index tested : " + indexStart + " direction : " + direction);

        for(int i = indexStart; i < idMax; i += direction)
        {
            ECellState cellSt = GameState.getPlayerMap(EPlayer.Player1).get(i).getCellState();

            if (cellSt != ECellState.Bombed)
                temp.add(i);        
            
            if (cellSt == ECellState.Bombed || (i + direction) >= idMax)
            {
                if (temp.size() > this.targetQueryIndex.size())
                {
                    this.targetQueryIndex.clear();
                    this.targetQueryIndex.addAll(temp);
                }

                temp.clear();
            }
        }
    }

    // utiliser deux integer (min et max) à la place de tout un tableau.
    private ArrayList <Integer> lastContiguousSuccessHit = new ArrayList <Integer> (10);

    private ArrayList <Integer> targetQueryIndex = new ArrayList <Integer> (10);
}
