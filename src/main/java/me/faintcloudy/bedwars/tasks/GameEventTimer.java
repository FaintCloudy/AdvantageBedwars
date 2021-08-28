package me.faintcloudy.bedwars.tasks;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class GameEventTimer extends BukkitRunnable {
    Game game = Bedwars.getInstance().game;

    @Override
    public void run() {
        game.nextEventTime--;
        if (game.nextEventTime <= 0)
        {
            game.nextEvent.onEvent();
            if (game.gameEvents.size() < (game.getEventOrder(game.nextEvent) + 2))
            {
                cancel();
                return;
            }
            game.nextEvent = game.gameEvents.get(game.getEventOrder(game.nextEvent) + 1);
            game.nextEventTime = game.nextEvent.time();
        }
    }
}
