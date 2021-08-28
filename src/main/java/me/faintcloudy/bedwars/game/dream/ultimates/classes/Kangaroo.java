package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.events.player.PlayerDeadEvent;
import me.faintcloudy.bedwars.events.team.TeamBedBrokeEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class Kangaroo implements UltimateClass {
    @Override
    public String displayName() {
        return "袋鼠";
    }

    @Override
    public void init(GamePlayer player) {

    }

    @EventHandler
    public void onBedBroke(TeamBedBrokeEvent event)
    {
        GamePlayer broker = event.broker;
        if (manager().getUltimatesClass(broker) == this)
        {
            broker.player.getInventory().addItem(new ItemBuilder(Material.MILK_BUCKET).setDisplayName(ChatColor.LIGHT_PURPLE + "魔法牛奶").build());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (event.getTo().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
        {
            if (manager().getUltimatesClass(player) != this)
                return;
            if (manager().isColddowning(player))
                return;
            player.player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDead(PlayerDeadEvent event)
    {
        GamePlayer player = event.player;
        int i = new Random().nextInt(99);
        if (manager().getUltimatesClass(player) != this)
            return;
        if (i >= 0 && i < 49)
        {

            event.drop = false;
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (manager().getUltimatesClass(player) == this)
        {
            if (manager().isColddowning(player))
                return;
            player.player.setAllowFlight(false);
            player.player.setFlying(false);
            event.setCancelled(true);
            this.jump(player);
            manager().resetColddown(player);
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            GamePlayer player = GamePlayer.get((Player) event.getEntity());
            if (manager().getUltimatesClass(player) == this)
            {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
                    event.setCancelled(true);
            }
        }
    }

    public void jump(GamePlayer player)
    {
        Vector v = player.player.getLocation().getDirection().multiply(2.0D).setY(1.3D);
        player.player.setVelocity(v);
    }

    @Override
    public int colddown() {
        return 10;
    }
}
