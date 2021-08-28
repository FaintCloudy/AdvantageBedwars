package me.faintcloudy.bedwars.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InventoryChangeEvent extends Event {
    public Player player;
    public static HandlerList handlerList = new HandlerList();
    public InventoryChangeEvent(Player player)
    {
        this.player = player;
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
}
