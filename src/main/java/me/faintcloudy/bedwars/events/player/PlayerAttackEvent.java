package me.faintcloudy.bedwars.events.player;

import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAttackEvent extends Event {
    public static HandlerList handlerList = new HandlerList();

    public GamePlayer solver;
    public GamePlayer attacker;
    public AttackType type;
    public PlayerAttackEvent(GamePlayer solver, GamePlayer attacker, AttackType type)
    {
        this.solver = solver;
        this.attacker = attacker;
        this.type = type;
    }

    public enum AttackType
    {
        HIT, BOW, SKILL
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
