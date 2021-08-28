package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.shopitem.SpecialItem;
import me.faintcloudy.bedwars.listener.SpecialItemListener;
import net.minecraft.server.v1_8_R3.BiomeForest;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Swordsman implements UltimateClass {

    @Override
    public String displayName() {
        return "剑客";
    }

    public HashMap<GamePlayer, Integer> dashingTimes = new HashMap<>();
    public HashMap<GamePlayer, Integer> backTimes = new HashMap<>();
    public HashMap<GamePlayer, Location> backLocations = new HashMap<>();

    @Override
    public void init(GamePlayer player) {
        new BukkitRunnable()
        {
            boolean await = false;
            public void run()
            {
                if (manager().getUltimatesClass(player) != SWORDSMAN)
                {
                    cancel();
                    return;
                }
                if (!player.player.getItemInHand().getType().name().contains("SWORD"))
                    return;
                if (!player.player.isBlocking())
                {
                    int time = dashingTimes.getOrDefault(player, 0);
                    if (time > 0)
                    {
                        dashingTimes.put(player, dashingTimes.getOrDefault(player, 0)-1);
                        if (!manager().isColddowning(player))
                        {
                            player.player.setLevel(0);
                            player.player.setExp(dashingTimes.getOrDefault(player, 0) / 40f);
                        }
                    }

                    return;
                }

                if (manager().isColddowning(player) && backLocations.containsKey(player) && !await)
                {
                    int backTime = backTimes.getOrDefault(player, 0);
                    backTimes.put(player, backTime + 1);
                    if (backTimes.getOrDefault(player, 0) >= 20)
                    {
                        back(player);
                    }
                    return;
                }
                if (manager().isColddowning(player))
                    return;

                int dashingTime = dashingTimes.getOrDefault(player, 0);
                dashingTimes.put(player, dashingTime + 1);
                player.player.setExp(dashingTimes.getOrDefault(player, 0) / 40f);
                player.player.playSound(player.player.getLocation(), Sound.NOTE_BASS, 1, 1);
                if (dashingTimes.getOrDefault(player, 0) >= 40)
                {
                    dash(player);
                    await = true;
                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            await = false;
                        }
                    }.runTaskLater(Bedwars.getInstance(), 6);
                    dashingTimes.put(player, 0);
                    manager().resetColddown(player);
                }


            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1);
    }

    private void dash(GamePlayer player)
    {
        backLocations.put(player, player.player.getLocation());

        Vector vector = player.player.getLocation().getDirection();
        SpecialItemListener.noFallDamagePlayers.add(player.player);
        player.player.setVelocity(vector.multiply(2));
    }

    private void back(GamePlayer player)
    {
        Location location = backLocations.get(player);
        player.safetyTeleport(location);
        backLocations.remove(player);
        backTimes.remove(player);
    }

    @Override
    public int colddown() {
        return 5;
    }
}
