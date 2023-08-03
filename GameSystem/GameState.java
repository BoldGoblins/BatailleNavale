package GameSystem;

import Actors.Cell;
import Actors.Player;
import Actors.Ship;
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

        if (parse(ship, EPlayer.Player1, index))
            return;
        
        // Si 1er tentative ne fonctionne pas, vérifier qu'on a pas déjà testé toutes les rotations possibles (sauf dernière possibilité qui a échouée).
        resetDirRotaShip();
        parse(ship, EPlayer.Player1, index);
    }

    // Shuffler les directions
    public boolean tryPlaceShipAt(EPlayer player, Ship ship, int index)
    {   
        resetDirRotaShip();

        if (player == EPlayer.Player2)
            Collections.shuffle(m_DirRotaShip);

        return parse(ship, player, index);
    }

    private boolean parse(Ship ship, EPlayer player, int index)
    {
        ArrayList <Integer> slots = new ArrayList <Integer> (ship.getLength());

        for(int i = 0; i < 4; ++i)
        {
            int dir = m_DirRotaShip.get(i);

            if (dir == 0)
                continue;

            int maxIndex = index + dir * (ship.getLength() - 1);

            if (maxIndex > 99 || maxIndex < 0)
                continue;

            // plus sur la même ligne.
            if ((dir == -1 || dir == 1) && (maxIndex / 10 != index / 10))
                continue;
            
            if (findContiguousCells(slots, ship.getLength(), ship.getHashCode(), index, m_DirRotaShip.get(i)))
            {
                if (player == EPlayer.Player1)
                {
                    removeShipFromHash(ship.getHashCode());
                    m_DirRotaShip.set(i, 0);
                }
          
                addShipFromHash(slots, ship.getHashCode(), player);
                return true;
            }

            // Possible ou pas possible, rotation déjà testée, sera réinitialisée si tout == 0.
            if (player == EPlayer.Player1)
                m_DirRotaShip.set(i, 0);
        }
        
        return false;
    }

    private boolean findContiguousCells(ArrayList <Integer> slots, int countLength, int shipHash, int index, int direction)
    {
        if (countLength == 0)
            return true;

        Cell cell = getPlayerMap(m_CurrentPlayer).get(index);

        if (cell.getCellState() == ECellState.Filled && cell.getShipHash() != shipHash)
            return false;

        if (cell.getCellState() == ECellState.Empty || cell.getShipHash() == shipHash)
        {
            if (findContiguousCells(slots, countLength - 1, shipHash, index + direction, direction))
            {
                slots.add(index);
                return true;
            }
        }

        return false; 
    }
    // fonction n'existe que pour le P1
    private void removeShipFromHash(int shipHash)
    {
        for (Cell c : m_MapPlayer1)
        {
            if (c.getShipHash() == shipHash)
                c.removeShip();
        }
    }

    private void addShipFromHash(ArrayList <Integer> slots, int shipHash, EPlayer player)
    {
        try
        {
            for(int index : slots)
            {
                getPlayerMap(player).get(index).addShip(shipHash);
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
        if (player == EPlayer.Player1)
            return m_MapPlayer1;
        else
            return m_MapPlayer2;
    }

    public Player getPlayer(EPlayer player)
    {
        if (player == EPlayer.Player1)
            return this.player1;
        else
            return this.player2;
    }

    private Player player1;
    private Player player2;

    static private EPlayer m_CurrentPlayer = EPlayer.Player1;

    static private ArrayList <Cell> m_MapPlayer1 = new ArrayList<Cell> (10 * 10);
    static private ArrayList <Cell> m_MapPlayer2 = new ArrayList<Cell> (10 * 10);

    // directions (left, up, right or down)
    private ArrayList <Integer> m_DirRotaShip = new ArrayList <Integer> (4);

}