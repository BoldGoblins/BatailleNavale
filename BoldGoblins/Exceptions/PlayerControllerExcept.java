package BoldGoblins.Exceptions;

import java.lang.RuntimeException;

public class PlayerControllerExcept extends RuntimeException
{
    public PlayerControllerExcept(String message)
    {
        super("Player Controller Exception : " + message);
    }
}
