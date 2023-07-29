package GameSystem;

import Actors.Cell;
import Actors.Player;
import Actors.Ship;
import BoldGoblins.Exceptions.GameModeExcept;
import Enums.ECellState;
import Enums.EPlayer;

import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.Collections;

public class GameState
{
    public GameState(String player1Name, String player2Name)
    {
        this.player1 = new Player(GameMode.initPlayerState(EPlayer.Player1), player1Name);
        this.player2 = new Player(GameMode.initPlayerState(EPlayer.Player2), player2Name);
  
        for (int i = 0; i < 100; ++i)
        {
            m_MapPlayer1.add(i, new Cell());
            m_MapPlayer2.add(i, new Cell());
        }

        int dir[] = {-1, -10, 1, +10};

        for (int i = 0; i < 4; ++i)
            m_DirRotaShip.add(dir[i]);
    }

    public void tryRotateShipAt(Ship ship, int index)
    {
        // Si nous n'avons pas le bon bateau dans la case depuis laquelle on veut le faire rotate.
        if (m_MapPlayer1.get(index).getShipHash() != ship.getHashCode() )
            return;

        if (ship.getLength() == 1)
            return;

        if (parse(ship, index, false))
            return;
        
        // Si 1er tentative ne fonctionne pas, vérifier qu'on a pas déjà testé toutes les rotations possibles (sauf dernière possibilité qui a échouée).
        resetDirRotaShip();
        parse(ship, index, false);
    }

    // Shuffler les directions
    public boolean tryPlaceShipAt(EPlayer player, Ship ship, int index)
    {   
        resetDirRotaShip();

        boolean bIsAi = false;

        if (player == EPlayer.Player2)
        {
            Collections.shuffle(m_DirRotaShip);
            bIsAi = true;
        }

        return parse(ship, index, bIsAi);
    }

    private boolean parse(Ship ship, int index, boolean bIsAi)
    {
        ArrayList <Integer> slots = new ArrayList <Integer> (ship.getLength());

        EPlayer player = EPlayer.Player1;

        if  (bIsAi)
            player = EPlayer.Player2;

        for(int i = 0; i < 4; ++i)
        {
            if (findContiguousCells(slots, ship.getLength(), ship.getHashCode(), index, m_DirRotaShip.get(i)))
            {
                if (!bIsAi)
                {
                    removeShipFromHash(ship.getHashCode());
                    m_DirRotaShip.set(i, 0);
                }
          
                addShipFromHash(slots, ship.getHashCode(), player);
                return true;
            }

            // Possible ou pas possible, rotation déjà testée, sera réinitialisée si tout == 0.
            if (!bIsAi)
                m_DirRotaShip.set(i, 0);
        }
        
        return false;
    }

    // direction could be -1, +1, -10 or + 10 (left, right, down or up)
    // function used only for translation, rotation will be handled in another one
    private boolean findContiguousCells(ArrayList <Integer> slots, int length, int shipHash, int index, int direction)
    {
        // System.err.println("Call : length = " + length + " Index = " + index + " Direction : " + direction);
        if (direction == 0)
            return false;

        if (length == 0)
            return true;

        if (index < 0 || index > 99)
            return false;

        // length - 1 parce qu'on calcule avec les index de 0 à 99 et length va de 1 à 10. 
        if ( ((index - (length - 1) * 10 < 0) && direction == -10) || ((index + (length - 1) * 10 > 99) && direction == 10) )
            return false;

        if ( ((length > (index % 10 + 1)) && direction == -1) || ((length > 10 - index % 10) && direction == 1) )
            return false;
        
        try
        {
            if ( (getPlayerMap(m_CurrentPlayer).get(index).getCellState() == ECellState.Filled) && (getPlayerMap(m_CurrentPlayer).get(index).getShipHash() != shipHash) )
                return false;

            if ( (getPlayerMap(m_CurrentPlayer).get(index).getCellState() == ECellState.Empty) || (getPlayerMap(m_CurrentPlayer).get(index).getShipHash() == shipHash) ) 
                if (findContiguousCells(slots, length - 1, shipHash, index + direction, direction))
                {
                    slots.add(index);
                    return true;
                }
        }
        catch(IndexOutOfBoundsException e)
        {
            System.err.println("Out of Bounds in GameState.findContiguousCells()");
            return false;
        }

        return false; 
    }
    // fonction n'existe que pour le P1
    private void removeShipFromHash(int shipHash)
    {
        for (Cell c : m_MapPlayer1)
        {
            if (c.getShipHash() == shipHash)
            {
                // System.err.println("Remove : " + c.getShipHash());
                c.removeShip();
                // System.err.println("After : " + c.getShipHash());
            }
        }
    }

    private void addShipFromHash(ArrayList <Integer> slots, int shipHash, EPlayer player)
    {
        try
        {
            for(int index : slots)
            {
                if (player == EPlayer.Player1)
                    m_MapPlayer1.get(index).addShip(shipHash);
                else
                    m_MapPlayer2.get(index).addShip(shipHash);
            }
        }
        catch (IndexOutOfBoundsException ex)
        {
            System.err.println("IndexOutOfBoundsExcept in GameState.defaultPlaceShip.");
            return;
        }
    }

    private void resetDirRotaShip()
    {
        m_DirRotaShip.set(0, -1);
        m_DirRotaShip.set(1, -10);
        m_DirRotaShip.set(2, 1);
        m_DirRotaShip.set(3, 10);
    }

    public void flipCurrentPlayer()
    {
        if (m_CurrentPlayer == EPlayer.Player1)
            m_CurrentPlayer = EPlayer.Player2;

        else
            m_CurrentPlayer = EPlayer.Player1;
    }

    public void setFirstPlayer (boolean bCoinToss)
    {
        if (bCoinToss)
            m_CurrentPlayer = EPlayer.Player1;
        else
            m_CurrentPlayer = EPlayer.Player2;
    }

    static public EPlayer getCurrentPlayer()
    {
        return m_CurrentPlayer;
    }

    public static ArrayList <Cell> getPlayerMap(EPlayer player)
    {
        switch(player)
        {
        case Player1 : return m_MapPlayer1;
        case Player2 : return m_MapPlayer2;
        default : return m_MapPlayer1;
        }
    }

    public Player getPlayer(EPlayer player)
    {
        if (player == EPlayer.Player1)
            return this.player1;
        else
            return this.player2;
    }

    // tout peut être static ...
    private Player player1;
    private Player player2;

    static private EPlayer m_CurrentPlayer = EPlayer.Player1;

    static private ArrayList <Cell> m_MapPlayer1 = new ArrayList<Cell> (10 * 10);
    static private ArrayList <Cell> m_MapPlayer2 = new ArrayList<Cell> (10 * 10);

    // directions (left, up, right or down)
    private ArrayList <Integer> m_DirRotaShip = new ArrayList <Integer> (4);

}