package Scenes;

import BoldGoblins.Utilitaires.BGTextFilesReader;
import BoldGoblins.Exceptions.SceneExcept;

import java.util.ArrayList;

public class Scene 
{
    public Scene(String fileName)
    {
        StringBuilder str = new StringBuilder("Text\\");

        str.append(fileName).append(".txt");
        
        BGTextFilesReader.loadTextFile(str.toString(), this.texture);
    }

    public void display()
    {
        if (this.texture.isEmpty())
            throw new SceneExcept("display", "texture array is empty.");

        for(StringBuilder str : this.texture)
        {
            System.out.println(str.toString());
        }
    }

    private ArrayList <StringBuilder> texture = new ArrayList <StringBuilder> (150);

}
