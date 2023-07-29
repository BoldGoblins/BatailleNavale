package BoldGoblins.Exceptions;

import java.lang.RuntimeException;

public class GameModeExcept extends RuntimeException
{
    public GameModeExcept(String message)
    {
        super("Game Mode Exception : " + message);
    }
}
