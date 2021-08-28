package me.faintcloudy.bedwars.events.player;

import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerGotKilledEvent extends PlayerDeadEvent
{
    public GamePlayer killer;
    public PlayerGotKilledEvent(GamePlayer player, GamePlayer killer, boolean isFinal) {
        super(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, isFinal);
        this.killer = killer;
        this.isFinal = isFinal;
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
