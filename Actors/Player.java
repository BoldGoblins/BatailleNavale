package Actors;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import Actors.Ship;

public class Player 
{
    public Player()
    {

    }

    public Player(ArrayList <Ship> arrList, String name)
    {
        this.mName = name;

        for(Ship ship : arrList)
            fleet.put(ship.getHashCode(), ship);
    }

    public void shoot(boolean bHit)
    {
        ++m_Shoots;

        if (bHit)
            ++m_Points;
    }
    
    public boolean hasLoose()
    {
        return fleet.size() <= 0;
    }

    public void shipSunk(int shipHash)
    {
        fleet.remove(shipHash);
    }

    public int getFleetCount()
    {
        return fleet.size();
    }

    public Ship getShip(int shipHash)
    {
        if (this.fleet.containsKey(shipHash))
            return this.fleet.get(shipHash);

        // sécurité : si on appelle des fonctions sur un Ship qui n'a pas été trouvé dans this.fleet (car détruit)
        // au moins ça ne fera pas crasher le programme.
        return new Ship("", 0);
    }

    public Collection <Ship> getFleet()
    {
        return fleet.values();
    }

    public String mName;
    private int m_Points = 0;
    private int m_Shoots = 0;

    private HashMap <Integer, Ship> fleet = new HashMap <Integer, Ship> (10);
}
