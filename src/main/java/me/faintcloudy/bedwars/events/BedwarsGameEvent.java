package me.faintcloudy.bedwars.events;

import me.faintcloudy.bedwars.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsGameEvent extends Event {
    public Game game;
    public BedwarsGameEvent(Game game)
    {
        this.game = game;
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
