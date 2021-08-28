package me.faintcloudy.bedwars.listener;

import fr.minuskube.inv.opener.ChestInventoryOpener;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.inventory.TargetSelectMenu;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class SpectateListener implements Listener {

    @EventHandler
    public void onExit(PlayerToggleSneakEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.SPECTATING)
            return;
        if (player.spectatorSettings.firstPersonTargeting)
        {
            player.spectatorSettings.firstPersonTargeting(false);
        }
    }


    @EventHandler
    public void onOpenChest(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
            GamePlayer player = GamePlayer.get(event.getPlayer());
            if (player.state != GamePlayer.PlayerState.ALIVE)
                event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
                return;
            GamePlayer player = GamePlayer.get((Player) event.getEntity());
            if (player.state != GamePlayer.PlayerState.ALIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event)
    {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()) || CitizensAPI.getNPCRegistry().isNPC(event.getDamager()))
            return;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player)
        {
            GamePlayer player = GamePlayer.get((Player) event.getEntity());
            GamePlayer damager = GamePlayer.get((Player) event.getDamager());
            if (damager.state != GamePlayer.PlayerState.ALIVE || player.state != GamePlayer.PlayerState.ALIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSelect(PlayerInteractEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.SPECTATING)
            return;
        if (player.spectatorSettings.firstPersonTargeting)
        {
            TargetSelectMenu.menu().open(player.player);
        }
    }

    @EventHandler
    public void onSpectateBreak(BlockBreakEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.SPECTATING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpectateBlock(BlockPlaceEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.SPECTATING)
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onSpectateArmorStand(PlayerArmorStandManipulateEvent event)
    {
        if (event.isCancelled())
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.SPECTATING)
            return;
        event.setCancelled(true);
    }

}
