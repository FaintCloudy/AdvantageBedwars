package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import net.citizensnpcs.nms.v1_12_R1.entity.nonliving.ThrownPotionController;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Physician implements UltimateClass {
    @Override
    public String displayName() {
        return "医师";
    }

    HashMap<GamePlayer, Integer> blockTimes = new HashMap<>();
    static ItemStack potion = new ItemBuilder(Potion
            .fromItemStack(new ItemBuilder(Material.POTION, 1, (byte)1).build())
            .splash().toItemStack(1))
            .addPotion(new PotionEffect(PotionEffectType.REGENERATION, 50, 2))
            .setDisplayName("§a医师药水 (0:33)").build();

    @Override
    public List<ItemStack> takeWith(GamePlayer player) {
        return Collections.singletonList(potion);
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity() instanceof ThrownPotion))
            return;
        if (!(event.getEntity().getShooter() instanceof Player))
            return;
        GamePlayer player = GamePlayer.get((Player) event.getEntity().getShooter());
        if (manager().getUltimatesClass(player) != this)
            return;
        manager().resetColddown(player);
        takePotion(player);
    }



    @EventHandler
    public void onEffect(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player))
            return;
        GamePlayer player = GamePlayer.get((Player) event.getPotion().getShooter());
        if (manager().getUltimatesClass(player) != this)
            return;
        if (event.getPotion().getEffects().equals(Potion.fromItemStack(potion).getEffects())) {
            event.getAffectedEntities().clear();

            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    GamePlayer p = GamePlayer.get((Player) entity);
                    if (p.getTeam() == player.getTeam()) {
                        event.getAffectedEntities().add(p.player);
                    }
                }
            }

        }
        this.takePotion(player);
    }

    public void takePotion(GamePlayer player)
    {
        player.player.getInventory().remove(potion);
    }

    @Override
    public void init(GamePlayer player) {

        player.player.getInventory().addItem(potion);
        new BukkitRunnable()
        {
            public void run()
            {
                if (manager().getUltimatesClass(player) != PHYSICIAN)
                {
                    takePotion(player);
                    cancel();
                    return;
                }
                if (manager().isColddowning(player))
                    return;
                if (!player.player.getInventory().contains(potion))
                {
                    player.player.getInventory().addItem(potion);
                }
                if (!player.player.isBlocking())
                    return;
                blockTimes.put(player, blockTimes.getOrDefault(player, 0)+1);
                if (blockTimes.getOrDefault(player, 0) >= 40)
                {
                    player.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, false, false));
                    player.player.playSound(player.player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                    player.player.sendMessage("§a§l你治疗了你自己！");
                    takePotion(player);
                    blockTimes.remove(player);
                    manager().resetColddown(player);
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1);
    }

    @Override
    public int colddown() {
        return 20;
    }
}
