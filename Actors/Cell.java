package Actors;
import Enums.ECellState;

public class Cell 
{
    public Cell()
    {
        m_State = ECellState.Empty;

        this.bShipDestroyed = false;
    }
    public void addShip(int ShipHash)
    {
        m_ShipHash = ShipHash;
        m_State = ECellState.Filled;
    }

    public void removeShip()
    {
        m_ShipHash = 0;
        m_State = ECellState.Empty;
    }

    public int getShipHash()
    {
        return m_ShipHash;
    }

    public ECellState getCellState()
    {
        return m_State;
    }

    public boolean getShipDestroyed()
    {
        return this.bShipDestroyed;
    }

    public void setBombed()
    {
        if (m_State == ECellState.Filled)
            this.bShipDestroyed = true;

        m_State = ECellState.Bombed;
    }

    private ECellState m_State;
    // Penser à vérifier dans la classe Ship que l'on ne peut pas avoir 2 noms identiques ...
    private int m_ShipHash = 0;

    // Si un Ship était présent dans la cellule avant qu'elle ne soit bombardée, cette valeur sera true.
    private boolean bShipDestroyed = false;
}