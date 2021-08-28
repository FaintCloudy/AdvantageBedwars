package me.faintcloudy.bedwars.events.player;


import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnergyChangeEvent extends Event {

    public GamePlayer gamePlayer;
    public int original;
    public int news;
    public PlayerEnergyChangeEvent(GamePlayer gamePlayer, int original, int news)
    {
        this.gamePlayer = gamePlayer;
        this.original = original;
        this.news = news;
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
