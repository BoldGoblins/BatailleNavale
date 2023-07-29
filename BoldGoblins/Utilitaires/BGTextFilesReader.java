package BoldGoblins.Utilitaires;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BGTextFilesReader 
{
    // throws InvalidPathException (Paths.get), IOException et SecurityException (Files.newBufferedReader)
    public static void loadTextFile(String path, ArrayList <StringBuilder> cnt)
    {
        try
        {
           Path f = Paths.get(path);
           BufferedReader bfr = Files.newBufferedReader(f);

           while(bfr.ready())
                cnt.add(new StringBuilder(bfr.readLine()));
        }
        catch (InvalidPathException ipe)
        {
            System.err.println("Invalid Path : " + path);
        }
        catch(Exception e)
        {
            System.err.println("Error in reading file : " + path);
        }
    }
    
}
