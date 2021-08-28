package me.faintcloudy.bedwars.scoreboard;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.tasks.GameBoardTimer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoardManager {
    Map<Player, GameBoardTimer> gameBoards = new HashMap<>();
    Bedwars plugin;
    Game game;
    public ScoreBoardManager(Bedwars plugin, Game game)
    {
        this.plugin = plugin;
        this.game = game;
    }

    public Map<Player, GameBoardTimer> getGameBoards() {
        return gameBoards;
    }

    public void unloadGameBoard(Player player) {
        if (gameBoards.get(player) == null)
            return;
        GameBoardTimer gameBoard = gameBoards.get(player);
        gameBoard.unload();
        gameBoards.remove(player);
    }

    public void loadGameBoard(Player player)  {
        if (gameBoards.containsKey(player))
        {
            gameBoards.get(player).unload();
            gameBoards.remove(player);
        }

        gameBoards.put(player, new GameBoardTimer(game, GamePlayer.get(player)));
    }
}
