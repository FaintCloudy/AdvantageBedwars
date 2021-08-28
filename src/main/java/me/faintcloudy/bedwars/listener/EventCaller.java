package me.faintcloudy.bedwars.listener;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.InventoryChangeEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class EventCaller implements Listener {

    public static void callInventoryChangeEvent(Player player)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                if (Bedwars.getInstance().game.state != GameState.GAMING)
                    return;
                GamePlayer.get(player).checkWoodSword();
                GamePlayer.get(player).checkSharpness();
            }
        }.runTaskLater(Bedwars.getInstance(), 10);
    }

    @EventHandler
    public void inventoryChange1(InventoryPickupItemEvent event)
    {
        if (event.isCancelled())
            return;

        if (event.getInventory() instanceof PlayerInventory)
        {
            Player player1 = (Player) event.getInventory().getHolder();
            GamePlayer player = GamePlayer.get(player1);
            if (player.state == GamePlayer.PlayerState.ALIVE)
            callInventoryChangeEvent(player1);
        }
    }

    @EventHandler
    public void inventoryChange4(PlayerPickupItemEvent event)
    {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        GamePlayer player1 = GamePlayer.get(player);
        if (player1.state == GamePlayer.PlayerState.ALIVE)
        callInventoryChangeEvent(player);
    }

    @EventHandler
    public void inventoryChange2(InventoryClickEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get((Player) event.getWhoClicked());
        if (player.state == GamePlayer.PlayerState.ALIVE)
            callInventoryChangeEvent((Player) event.getWhoClicked());
    }

    @EventHandler
    public void inventoryChange3(InventoryCreativeEvent event)
    {
        if (event.isCancelled())
            return;

        GamePlayer player = GamePlayer.get((Player) event.getWhoClicked());
        if (player.state == GamePlayer.PlayerState.ALIVE)
        callInventoryChangeEvent((Player) event.getWhoClicked());
    }

    @EventHandler
    public void inventoryChange5(PlayerDropItemEvent event)
    {
        if (event.isCancelled())
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state == GamePlayer.PlayerState.ALIVE)
        callInventoryChangeEvent(event.getPlayer());
    }
}
