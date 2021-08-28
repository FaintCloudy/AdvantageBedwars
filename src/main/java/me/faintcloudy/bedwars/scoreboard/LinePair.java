package me.faintcloudy.bedwars.scoreboard;

public class LinePair
{
    private final String line;
    private final int score;

    private LinePair(String line, int score)
    {
        this.line = line;
        this.score = score;
    }

    public static LinePair of(String line, int i)
    {
        return new LinePair(line, i);
    }

    public String getLine()
    {
        return line;
    }

    public int getScore()
    {
        return score;
    }

    public String getText()
    {
        return line;
    }
}
