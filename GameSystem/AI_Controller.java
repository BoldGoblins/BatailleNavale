package GameSystem;

import Actors.Ship;
import Enums.EPlayer;
import Enums.ECellState;
import BoldGoblins.Exceptions.AI_ControllerExcept;
import BoldGoblins.Utilitaires.BGRandomiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



public class AI_Controller
{
    public int playMove(GameState gs)
    {
        // Un Ship déjà touché est encore vivant
        if (boundsAreInit())
        {
            if (!onlyOneShot())
            {
                int dir = getTargetDirection();

                int frontIndex = this.lastSuccessHitsMin - dir;
                int endIndex = this.lastSuccessHitsMax + dir;

                boolean bFrontValid = false;
                boolean bEndValid = false;

                if (frontIndex >= 0 && frontIndex / 10 == this.lastSuccessHitsMin / 10)
                {
                    if (GameState.getPlayerMap(EPlayer.Player1).get(frontIndex).getCellState() != ECellState.Bombed)
                        bFrontValid = true;  
                }                
                if (endIndex < 100 && endIndex / 10 == this.lastSuccessHitsMax / 10)
                {
                    if (GameState.getPlayerMap(EPlayer.Player1).get(endIndex).getCellState() != ECellState.Bombed)
                        bEndValid = true;
                } 

                // DEBUG
                // System.err.println("Ship Query : from " + frontIndex + " to " + endIndex);

                // pas encore testé (bombé) un des deux (et les deux sont possibles).
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

            // 1 seul coup donc (min = max).
            int shipHash = GameState.getPlayerMap(EPlayer.Player1).get(this.lastSuccessHitsMin).getShipHash();

            return randomSelectionAlongLastPos(this.lastSuccessHitsMin, gs.getPlayer(EPlayer.Player1).getShip(shipHash).getLength());
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
                saveLastSuccessPos(index);

            else
            {
                if (this.oldSuccessfulHits.size() > 0)
                {
                    this.lastSuccessHitsMin = (int) this.oldSuccessfulHits.keySet().toArray()[0];
                    this.lastSuccessHitsMax = (int) this.oldSuccessfulHits.get(this.lastSuccessHitsMin);

                    this.oldSuccessfulHits.remove(this.lastSuccessHitsMin);

                    // DEBUG : 
                    // System.err.println("Query OldHit : " + this.lastSuccessHitsMin + " min  et max : " + this.lastSuccessHitsMax);
                }
                else
                    resetBounds();
            }             
        }        
    }

    // Ne doit être appelé que si le Ship qui a déjà été touché est encore .alive()
    // Le Ship n'a été touché qu'une seule fois ! On a donc pas encore idée de sa direction à ce stade.
    // shipLength ne peut être == 0.
    // La longueur du Ship >= 2. Si elle valait 1, alors le Ship !.alive() au moment de l'appel de cette fonction.
    private int randomSelectionAlongLastPos(int lastSuccessHit, int shipLength)
    {
        HashMap <Integer, Float> directions = new HashMap <Integer, Float> (4);

        directions.put(1, 0.00f);
        directions.put(-1, 0.00f);
        directions.put(10, 0.00f);
        directions.put(-10, 0.00f);

        for(int dir : directions.keySet())
        {
            int temp = lastSuccessHit;
            int countCell = 0;

            while(temp + dir >= 0 && temp + dir < 100)
            {
                temp += dir;

                // plus sur la même ligne.
                if ((dir == -1 || dir == 1) && (temp / 10 != lastSuccessHit / 10))
                    break;

                if (GameState.getPlayerMap(EPlayer.Player1).get(temp).getCellState() == ECellState.Bombed)
                    break;

                ++countCell;
            }

            directions.replace(dir, Float.valueOf((float) countCell / (float) shipLength));
        }

        Iterator <Integer> itDir = directions.keySet().iterator();
        Integer bestDir = itDir.next();

        // /!\ On commence à it + 1.
        while (itDir.hasNext())
        {
            Integer nextDir = itDir.next();
            
            if (directions.get(nextDir) > directions.get(bestDir))
                bestDir = nextDir;
        }

        // DEBUG : 
        // System.err.println("Single Pos Query : " + lastSuccessHit + " Direction :  " + bestDir + " Score : " + directions.get(bestDir));
        return lastSuccessHit + bestDir;
    }

    private int queryAdverseMap()
    {
        this.targetQueryIndex.clear();

        for(int i = 0; i < 10; ++i)
        { 
            // parse lines : 
            parseMap(i * 10, 1);

            // parse col :
            parseMap(i, 10);
        }

        // size 10 -> index 5, size 1 -> index 0
        int idArrTarget = this.targetQueryIndex.size() / 2;
        // max 3, min 0.
        int randBound = this.targetQueryIndex.size() / 3;
        int secureCount = 5000;

        while(--secureCount > 0)
        {
            if (BGRandomiser.getRandomBool())
                idArrTarget += BGRandomiser.getRandomInt(randBound);
            else
                idArrTarget -= BGRandomiser.getRandomInt(randBound);

            if (idArrTarget >= 0 && idArrTarget < this.targetQueryIndex.size())
            {
                if (GameState.getPlayerMap(EPlayer.Player1).get(this.targetQueryIndex.get(idArrTarget)).getCellState() == ECellState.Bombed)
                    continue;
                else
                    break;
            }
        }

        if (secureCount <= 0)
            return 0;

        // DEBUG : 
        // System.err.println("QueryMap : " + this.targetQueryIndex.get(idArrTarget));

        return this.targetQueryIndex.get(idArrTarget);
    }

    // direction : + 1 ou + 10
    private void parseMap(int indexStart, int direction)
    {
        ArrayList <Integer> temp = new ArrayList <Integer> (10);

        int idMax = indexStart + (direction * 10);

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

    private void saveLastSuccessPos(int index)
    {
        int newShipHash = GameState.getPlayerMap(EPlayer.Player1).get(index).getShipHash();

        if (!boundsAreInit())
        {
            this.lastSuccessHitsMin = index;
            this.lastSuccessHitsMax = index;
        }
        // check l'index Min arbitrairement : les deux index ont été initialisés ensemble et pointent obligatoirement sur une case avec le même Ship.
        else if (GameState.getPlayerMap(EPlayer.Player1).get(this.lastSuccessHitsMin).getShipHash() == newShipHash)
        {
            if (index > this.lastSuccessHitsMax)
                this.lastSuccessHitsMax = index;

            else if (index < this.lastSuccessHitsMin)
                this.lastSuccessHitsMin = index;
        }
        else
        {
            this.oldSuccessfulHits.put(this.lastSuccessHitsMin, this.lastSuccessHitsMax);

            this.lastSuccessHitsMin = index;
            this.lastSuccessHitsMax = index;
        }
    }

    private void resetBounds()
    {
        this.lastSuccessHitsMin = -1;
        this.lastSuccessHitsMax = 100;
    }

    private boolean boundsAreInit()
    {
        return this.lastSuccessHitsMin != -1 && this.lastSuccessHitsMax != 100;
    }

    // return false si non init
    private boolean onlyOneShot()
    {
        return this.lastSuccessHitsMin == this.lastSuccessHitsMax;
    }

    private int getTargetDirection()
    {
        if (this.lastSuccessHitsMax - this.lastSuccessHitsMin < 10)
            return 1;
        else
            return 10;
    }


    // -1 et 100 -> no-value; si init, les deux index pointent vers une case qui a le même shipHash forcément.
    private int lastSuccessHitsMin = -1;
    private int lastSuccessHitsMax = 100;

    // initialCapacity = 5 car possible uniquement d'avoir touché une ou plusieurs fois chaque Ship.
    // Map <indexMin, indexMax>
    private HashMap <Integer, Integer> oldSuccessfulHits = new HashMap <Integer, Integer> (5);

    // utilisé par la fonction queryAdverseMap() et parseMap().
    private ArrayList <Integer> targetQueryIndex = new ArrayList <Integer> (10);
}
