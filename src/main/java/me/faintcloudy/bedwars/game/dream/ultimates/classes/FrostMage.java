package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.player.PlayerGotKilledEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class FrostMage implements UltimateClass {
    @Override
    public String displayName() {
        return "冰霜法师";
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
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1);
    }

    @EventHandler
    public void onThrowPotion(ProjectileLaunchEvent event)
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

    public void takePotion(GamePlayer player)
    {
        player.player.getInventory().remove(potion);
    }

    static ItemStack potion = new ItemBuilder(Potion
            .fromItemStack((new ItemBuilder(Material.POTION, 1, (byte)4)).build())
            .splash().toItemStack(1))
            .addPotion(new PotionEffect(PotionEffectType.SLOW, 160, 1))
            .setDisplayName("§a冰霜法师药水").build();
    @Override
    public List<ItemStack> takeWith(GamePlayer player) {
        return Collections.singletonList(potion);
    }

    @EventHandler
    public void onKill(PlayerGotKilledEvent e) {
        if (manager().getUltimatesClass(e.killer) != this)
            return;
        int snowball = 0;

        for (ItemStack itemStack : e.killer.player.getInventory()){
            if (itemStack != null && itemStack.getType() == Material.SNOW_BALL) {
                snowball += itemStack.getAmount();
            }
        }

        if (snowball < 16) {
            e.killer.player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
        }

    }

    @Override
    public int colddown() {
        return 20;
    }
}
