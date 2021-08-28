package me.faintcloudy.bedwars.tasks;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameStateCheckTimer {
    public Game game;
    public GameStateCheckTimer(Game game)
    {
        this.game = game;
    }
    public void start()
    {
        new BukkitRunnable()
        {
            public void run()
            {
                List<Team> aliveTeams = new ArrayList<>();
                for (Team team : game.teams)
                {
                    if (team.state == TeamState.DEAD)
                        continue;
                    team.checkState();
                    if (team.state != TeamState.DEAD)
                        aliveTeams.add(team);
                }

                if (aliveTeams.size() < 2)
                {
                    if (!game.gameCheck)
                        return;
                    game.onEnd();
                    cancel();
                }

                if (GamePlayer.getOnlineGamePlayers().isEmpty())
                {
                    if (!game.gameCheck)
                        return;
                    game.onEnd();
                    cancel();
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 5);
    }
}
