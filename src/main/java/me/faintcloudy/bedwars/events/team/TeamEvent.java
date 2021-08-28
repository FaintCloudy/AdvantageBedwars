package me.faintcloudy.bedwars.events.team;

import me.faintcloudy.bedwars.events.BedwarsGameEvent;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.event.HandlerList;

public class TeamEvent extends BedwarsGameEvent {
    public Team team;
    public TeamEvent(Game game, Team team) {
        super(game);
        this.team = team;
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
