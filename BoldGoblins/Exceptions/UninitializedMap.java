package BoldGoblins.Exceptions;

import java.lang.RuntimeException;

public class UninitializedMap extends RuntimeException
{
    public UninitializedMap()
    {
        super("Attempt to display an uninitialized Map. Call Map.Init() before.");
    }
}
