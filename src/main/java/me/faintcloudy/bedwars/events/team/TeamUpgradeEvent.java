package me.faintcloudy.bedwars.events.team;

import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import org.bukkit.event.HandlerList;

public class TeamUpgradeEvent extends TeamEvent {
    public TeamUpgrade upgrade;
    public TeamUpgradeEvent(Team team, TeamUpgrade upgrade)
    {
        super(team.game, team);
        this.upgrade = upgrade;
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
