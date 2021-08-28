package me.faintcloudy.bedwars.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SidebarBoard extends Board
{
    private final Plugin plugin;
    private final Objective objective;
    private int taskId;
    private Body body;
    private String head;
    private List<String> bodyText;

    public SidebarBoard(Plugin plugin, Scoreboard scoreboard)
    {
        super(scoreboard);
        this.plugin = plugin;
        this.bodyText = new ArrayList<>();
        objective = getObjectiveOf(DisplaySlot.SIDEBAR);
    }

    public static SidebarBoard of(Plugin plugin)
    {
        return new SidebarBoard(plugin,
                plugin.getServer().getScoreboardManager().getNewScoreboard());
    }

    public static SidebarBoard of(Plugin plugin, Player p)
    {
        return of(plugin, getScoreboardOf(p));
    }

    public static SidebarBoard of(Plugin plugin, Scoreboard scoreboard)
    {
        return new SidebarBoard(plugin, scoreboard);
    }

    @Override public void update()
    {
        String headText = nil(head) ? null : head;
        if (!objective.getDisplayName().equals(headText))
        {
            objective.setDisplayName(headText);
        }

        List<String> lastBody = bodyText;
        bodyText = new ArrayList<>();

        if (nil(lastBody))
        {
            if (!nil(body))
                body.getList().forEach(pair -> {
                    String line = pair.getText();
                    bodyText.add(line);
                    objective.getScore(line).setScore(pair.getScore());
                });
        }
        else
        {
            if (!nil(body))
                body.getList().forEach(pair -> {
                    String line = pair.getText();
                    lastBody.remove(line);
                    bodyText.add(line);
                    objective.getScore(line).setScore(pair.getScore());
                });
            lastBody.forEach(scoreboard::resetScores);
        }
    }

    public void update(Supplier<Boolean> condition, int interval)
    {
        if (taskId == 0)
        {
            taskId = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if (condition.get())
                {
                    update();
                }
                else
                {
                    cancel();
                }
            }, 0, interval).getTaskId();
        }
    }

    public void setHead(String head)
    {
        this.head = head;
    }

    public void setBody(Body body)
    {
        this.body = body;
    }

    public void cancel()
    {
        if (taskId != 0)
        {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
