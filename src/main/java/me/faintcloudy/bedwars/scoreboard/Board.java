package me.faintcloudy.bedwars.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class Board
{
    protected final Scoreboard scoreboard;

    protected Board(Scoreboard scoreboard)
    {
        this.scoreboard = scoreboard;
    }

    public static boolean nil(Object i)
    {
        return i == null;
    }

    public static Scoreboard getScoreboardOf(Player p)
    {
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager()
                .getNewScoreboard();
        p.setScoreboard(scoreboard);
        return scoreboard;
    }

    public Objective getObjectiveOf(DisplaySlot slot)
    {
        Objective objective = scoreboard.registerNewObjective("board", "dummy");
        objective.setDisplaySlot(slot);
        return objective;
    }

    public Scoreboard getScoreboard()
    {
        return scoreboard;
    }

    public abstract void update();
}
