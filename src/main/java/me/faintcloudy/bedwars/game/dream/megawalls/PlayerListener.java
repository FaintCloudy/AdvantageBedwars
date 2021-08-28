package me.faintcloudy.bedwars.game.dream.megawalls;

import me.faintcloudy.bedwars.events.player.PlayerAttackEvent;
import me.faintcloudy.bedwars.events.player.PlayerDeadEvent;
import me.faintcloudy.bedwars.game.GamePlayer;

import me.faintcloudy.bedwars.game.dream.megawalls.classes.Archer;
import me.faintcloudy.bedwars.game.dream.megawalls.classes.ArmorSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    @EventHandler
    public void onGiveEnergy(PlayerAttackEvent event)
    {
        MegaWallsModeManager.getInstance().addEnergy(event.attacker, MegaWallsModeManager.getInstance().getSelectedClass(event.attacker).everyHitEnergy());
        if (event.type == PlayerAttackEvent.AttackType.BOW && MegaWallsModeManager.getInstance().getSelectedClass(event.attacker) instanceof Archer)
            MegaWallsModeManager.getInstance().addEnergy(event.attacker, ((Archer) MegaWallsModeManager.getInstance().getSelectedClass(event.attacker)).arrowEnergy());
    }

    @EventHandler
    public void onClassItemDrop(ItemSpawnEvent event)
    {
        if (ArmorSet.isClassItem(event.getEntity().getItemStack()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDropClassItem(PlayerDropItemEvent event)
    {
        if (ArmorSet.isClassItem(event.getItemDrop().getItemStack()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMoveClassItem(InventoryMoveItemEvent event)
    {
        if (event.getDestination() != event.getSource())
        {
            if (ArmorSet.isClassItem(event.getItem()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSkillDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntity().getType() != EntityType.PLAYER)
            return;
        if (!event.getDamager().hasMetadata("skill"))
            return;
        GamePlayer shooter = GamePlayer.get(Bukkit.getPlayer(event.getDamager().getMetadata("skill").get(0).asString().split(":")[1]));
        GamePlayer player = GamePlayer.get((Player) event.getEntity());
        if (player.getTeam() == shooter.getTeam())
            event.setCancelled(true);
    }

    @EventHandler
    public void onSkillRelease(PlayerInteractEvent event)
    {
        GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {

            if (gamePlayer.state != GamePlayer.PlayerState.ALIVE)
                return;
            if (!MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer).releaseMaterials().contains(event.getMaterial()))
                return;
            if (MegaWallsModeManager.getInstance().getEnergy(gamePlayer) >= 100)
            {
                boolean success = MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer).onSkill(gamePlayer);
                if (success)
                {
                    MegaWallsModeManager.getInstance().resetEnergy(gamePlayer);
                    gamePlayer.actionBarText = MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer).getActionBarText(gamePlayer);
                    return;
                }

                gamePlayer.sendMessage(ChatColor.RED + "没有目标在范围内");
            }
        }
        else
        {
            if (gamePlayer.state != GamePlayer.PlayerState.ALIVE)
                return;
            if (event.getMaterial() != Material.BOW)
                return;
            if (MegaWallsModeManager.getInstance().getEnergy(gamePlayer) >= 100)
            {
                boolean success = MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer).onSkill(gamePlayer);
                if (success)
                {
                    MegaWallsModeManager.getInstance().resetEnergy(gamePlayer);
                    gamePlayer.actionBarText = MegaWallsModeManager.getInstance().getSelectedClass(gamePlayer).getActionBarText(gamePlayer);
                    return;
                }

                gamePlayer.sendMessage(ChatColor.RED + "没有目标在范围内");
            }
        }
    }





    /*@EventHandler
    public void onExplodeProtect1(BlockExplodeEvent event)
    {
        World world = event.getBlock().getWorld();
        if (world.getGameRuleValue("mobGriefing").equals("false"))
        {
            event.setCancelled(true);
            world.setGameRuleValue("mobGriefing", "true");
        }
    }

    @EventHandler
    public void onExplodeProtect2(EntityExplodeEvent event)
    {
        World world = event.getLocation().getWorld();
        if (world.getGameRuleValue("mobGriefing").equals("false"))
        {
            event.setCancelled(true);
            world.setGameRuleValue("mobGriefing", "true");
        }
    }*/



}
