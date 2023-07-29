package Actors;

public class Ship 
{
    public Ship(String name, int length)
    {
        m_Name = name;
        m_Length = length;
    }

    public void hit()
    {
        --m_Length;

        if (m_Length <= 0)
            m_BAlive = false;
            
    }

    public int getLength()
    {
        return m_Length;
    }
    
    public boolean isAlive()
    {
        // sécurité (si on appelle cette fonction sur un bateau qui a été détruit).
        if (this.m_Length <= 0)
            this.m_BAlive = false;

        return m_BAlive;
    }

    public String getName()
    {
        return m_Name;
    }

    public int getHashCode()
    {
        return m_Name.hashCode();
    }

    private boolean m_BAlive = true;
    private int m_Length;
    private String m_Name;
}
