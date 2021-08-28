package me.faintcloudy.bedwars.events;

import me.faintcloudy.bedwars.game.Game;
import org.bukkit.event.HandlerList;

public class GameStartedEvent extends BedwarsGameEvent {
    public GameStartedEvent(Game game) {
        super(game);
    }

    public static HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
}
