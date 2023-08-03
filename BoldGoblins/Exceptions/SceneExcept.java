package BoldGoblins.Exceptions;

import java.lang.RuntimeException;

public class SceneExcept extends RuntimeException
{
    public SceneExcept(String method, String message)
    {
        super("SceneExcept in " + method + " : " + message);
    }
}
