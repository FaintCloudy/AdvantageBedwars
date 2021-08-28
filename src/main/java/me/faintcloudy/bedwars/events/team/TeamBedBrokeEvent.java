package me.faintcloudy.bedwars.events.team;

import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import org.bukkit.event.HandlerList;

public class TeamBedBrokeEvent extends TeamEvent {
    public GamePlayer broker;
    public TeamBedBrokeEvent(Game game, Team team, GamePlayer broker) {
        super(game, team);
        this.broker = broker;
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
