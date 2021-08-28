package me.faintcloudy.bedwars.events;

import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.event.HandlerList;

public class GameEndedEvent extends BedwarsGameEvent {
    public Team winner;
    public GameEndedEvent(Game game, Team winner) {
        super(game);
        this.winner = winner;
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
