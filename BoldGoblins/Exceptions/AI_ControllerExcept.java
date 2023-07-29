package BoldGoblins.Exceptions;

import java.lang.RuntimeException;

public class AI_ControllerExcept extends RuntimeException
{
    public AI_ControllerExcept(String method, String message)
    {
        super("AI_ControllerExcept in " + method + " : " + message);
    }
}
