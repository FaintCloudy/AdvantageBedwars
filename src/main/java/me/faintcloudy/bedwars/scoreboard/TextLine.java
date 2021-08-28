package me.faintcloudy.bedwars.scoreboard;

public class TextLine
{
    private final String text;

    private TextLine(String text)
    {
        this.text = text;
    }

    public static String of(String text)
    {
        return text;
    }

    public String getText()
    {
        return text;
    }

    public String toString()
    {
        return text;
    }
}
