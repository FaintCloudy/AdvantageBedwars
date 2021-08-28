package me.faintcloudy.bedwars.events.player;

import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.HandlerList;

public class PlayerSpawnEvent extends GamePlayerEvent {
    public PlayerSpawnEvent(GamePlayer player) {
        super(player);
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
