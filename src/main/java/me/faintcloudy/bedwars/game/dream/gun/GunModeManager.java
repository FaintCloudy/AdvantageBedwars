package me.faintcloudy.bedwars.game.dream.gun;

import com.google.common.collect.Sets;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.utils.LocationUtils;
import me.faintcloudy.bedwars.utils.ParticleEffects;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GunModeManager implements DreamManager, Listener {

    public GunModeManager()
    {

    }

    public static GunModeManager getInstance()
    {
        return (GunModeManager) Bedwars.getInstance().game.mode.manager;
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event)
    {
        Bukkit.getPluginManager().registerEvents(this, Bedwars.getInstance());
    }

    public void shoot(GamePlayer player, Gun gun)
    {
        if (gun == null)
            return;
        if (gun.loadings.getOrDefault(player, 0) > 0)
            return;
        if (gun.shootingCD.getOrDefault(player, 0) > 0)
            return;
        int amounts = gun.amounts.getOrDefault(player, gun.clipAmount);
        if (amounts <= 0)
        {
            this.loadGun(player, gun);
            return;
        }

        player.player.playSound(player.player.getLocation(), Sound.NOTE_BASS_DRUM, 1, 1);
        for (Block block : player.player.getLineOfSight(Sets.newHashSet(Material.AIR), (int) gun.range))
        {
            if (block.getType() != Material.AIR)
                continue;
            ParticleEffects.CRIT.display(0.0F, 0.0F, 0.0F, 0.0F, 3, block.getLocation(), 10.0D);
            List<GamePlayer> targets = new ArrayList<>();
            for (Player other : LocationUtils.getNearbyPlayers(block.getLocation(), 1.5))
            {
                GamePlayer gameOther = GamePlayer.get(other);
                if (gameOther.state == GamePlayer.PlayerState.ALIVE && gameOther.getTeam() != player.getTeam())
                    targets.add(gameOther);
            }
            GamePlayer nearest = null;
            for (GamePlayer target : targets)
            {
                if (nearest == null)
                {
                    nearest = target;
                    continue;
                }
                if (target.player.getLocation().distance(player.player.getLocation()) <
                        nearest.player.getLocation().distance(player.player.getLocation()))
                {
                    nearest = player;
                }
            }

            if (nearest == null)
                continue;

            nearest.smartDamage(gun.damage);

            player.player.playSound(player.player.getLocation(), Sound.ORB_PICKUP, 1, 1);

            gun.shootingCD.put(player, (int) gun.shootingSpeed * 20);
            break;

        }


    }

    public void loadGun(GamePlayer player, Gun gun)
    {
        gun.loadings.put(player, (int) gun.loading * 20);
        ItemStack gunItem = player.player.getItemInHand();
        gunItem.setDurability((short) 1);
        int ticks = (int) gun.loading * 20;
        int each = gunItem.getDurability() / ticks;
        new BukkitRunnable()
        {
            public void run()
            {
                if (gunItem.getDurability() < gunItem.getType().getMaxDurability())
                {
                    gunItem.setDurability((short) (gunItem.getDurability() + each));
                    player.player.getInventory();
                }
                else
                {
                    player.player.playSound(player.player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
                    cancel();
                }

            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1);
    }

    @EventHandler
    public void onLoadGun(PlayerInteractEvent event)
    {

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            return;
        if (!holdingGun(player.player))
            return;
        if (!event.getAction().name().contains("LEFT"))
            return;
        loadGun(player, identifyGun(player.player.getItemInHand()));
    }

    @EventHandler
    public void onShoot(PlayerInteractEvent event)
    {

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            return;

        if (!holdingGun(player.player))
            return;

        if (!event.getAction().name().contains("RIGHT"))
            return;

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);
        shoot(player, identifyGun(event.getPlayer().getItemInHand()));
    }

    @Override
    public DreamShop shop() {
        return new GunShop();
    }

    public boolean holdingGun(Player player)
    {
        return identifyGun(player.getItemInHand()) != null;
    }

    public Gun identifyGun(ItemStack item)
    {
        if (!item.hasItemMeta())
            return null;
        for (Gun gun : Gun.values())
        {
            if (item.getItemMeta().getDisplayName().equals(gun.toItemStack().getItemMeta().getDisplayName()))
                return gun;
        }
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public String[] startMessage() {
        return new String[0];
    }
}
