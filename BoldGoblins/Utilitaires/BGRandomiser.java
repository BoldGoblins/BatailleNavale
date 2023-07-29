package BoldGoblins.Utilitaires;

import java.util.Random;
import java.time.Clock;


public class BGRandomiser 
{
    static public int getRandomInt(int bound)
    {
        Random rand = new Random(Clock.systemUTC().millis());

        return rand.nextInt(bound);
    }

    static public boolean getRandomBool()
    {
        Random rand = new Random(Clock.systemUTC().millis());

        return rand.nextBoolean();
    }
}
