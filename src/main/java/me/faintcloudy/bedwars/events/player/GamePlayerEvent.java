package me.faintcloudy.bedwars.events.player;

import me.faintcloudy.bedwars.events.BedwarsGameEvent;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.HandlerList;

public class GamePlayerEvent extends BedwarsGameEvent {
    public GamePlayer player;
    public GamePlayerEvent(GamePlayer player) {
        super(player.game);
        this.player = player;
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
