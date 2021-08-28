package me.faintcloudy.bedwars.tasks;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.scoreboard.FixedBody;
import me.faintcloudy.bedwars.scoreboard.Scoreboards;
import me.faintcloudy.bedwars.scoreboard.SidebarBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameBoardTimer {
    GamePlayer player;
    SidebarBoard board;
    BukkitRunnable updateTimer;
    SimpleDateFormat format;
    Game game;

    public GameBoardTimer(Game game, GamePlayer player)  {
        this.game = game;
        this.player = player;
        format = new SimpleDateFormat("MM/dd/yy");
        initScoreBoard();
        this.game = Bedwars.getInstance().game;
        updateTimer = new BukkitRunnable() {
            @Override
            public void run() {
                updateScore();
            }
        };
        updateTimer.runTaskTimerAsynchronously(Bedwars.getInstance(), 0, 1);
    }



    public void initScoreBoard()  {
        SidebarBoard board = SidebarBoard.of(Bedwars.getInstance(), player.player);
        board.setHead("§e§l起床战争");

        board.setBody(FixedBody.of(game.state == GameState.WAITING ? Scoreboards.getWaitingBoard() : Scoreboards.getGamingBoard(player)));

        this.board = board;
        player.player.setScoreboard(board.getScoreboard());
    }

    private void updateScore()
    {
        board.setBody(FixedBody.of(
                game.state == GameState.WAITING ?
                Scoreboards.getWaitingBoard() :
                Scoreboards.getGamingBoard(player)));

        board.update();
    }

    public void unload()
    {
        updateTimer.cancel();
        player.player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }


}
