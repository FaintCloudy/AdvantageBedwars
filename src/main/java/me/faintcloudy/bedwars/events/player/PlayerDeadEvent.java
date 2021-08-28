package me.faintcloudy.bedwars.events.player;

import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDeadEvent extends GamePlayerEvent {
    public EntityDamageEvent.DamageCause cause;
    public boolean isFinal;
    public boolean drop = true;
    public PlayerDeadEvent(GamePlayer player, EntityDamageEvent.DamageCause cause, boolean isFinal) {
        super(player);
        this.cause = cause;
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
