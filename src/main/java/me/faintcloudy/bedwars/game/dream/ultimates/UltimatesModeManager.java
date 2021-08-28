package me.faintcloudy.bedwars.game.dream.ultimates;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.events.player.PlayerSpawnEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.game.dream.ultimates.classes.UltimateClass;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class UltimatesModeManager implements DreamManager, Listener {
    public boolean ultimatesEnabled = false;
    private final HashMap<GamePlayer, UltimateClass> playerUltimateClasses = new HashMap<>();
    private final HashMap<GamePlayer, Integer> colddowns = new HashMap<>();

    public void setUltimatesClass(GamePlayer player, UltimateClass ultimateClass)
    {
        this.getUltimatesClass(player).disable(player);
        this.getUltimatesClass(player).takeWith(player).forEach(item -> player.player.getInventory().remove(item));
        playerUltimateClasses.put(player, ultimateClass);
        colddowns.put(player, 0);
        ultimateClass.init(player);
    }

    public boolean isColddowning(GamePlayer player)
    {
        return this.getColddown(player) > 0;
    }

    public void resetColddown(GamePlayer player)
    {
        colddowns.put(player, this.getUltimatesClass(player).colddown() * 20);
    }

    public int getColddown(GamePlayer player)
    {
        return colddowns.getOrDefault(player, 0);
    }

    public UltimateClass getUltimatesClass(GamePlayer player)
    {
        return playerUltimateClasses.getOrDefault(player, UltimateClass.SWORDSMAN);
    }

    @EventHandler
    public void onSpawn(PlayerSpawnEvent event)
    {
        getUltimatesClass(event.player).takeWith(event.player).forEach(item -> event.player.player.getInventory().addItem(item));
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                Bukkit.broadcastMessage("§a§l超能力已启用！");
                register(UltimateClass.SWORDSMAN);
                register(UltimateClass.BUILDER);
                register(UltimateClass.COLLECTOR);
                register(UltimateClass.DESTROYER);
                register(UltimateClass.FROST_MAGE);
                register(UltimateClass.KANGAROO);
                register(UltimateClass.PHYSICIAN);
                ultimatesEnabled = true;
                for (GamePlayer player : Bedwars.getInstance().game.getAlive())
                    getUltimatesClass(player).init(player);
                new BukkitRunnable()
                {
                    public void run()
                    {
                        for (GamePlayer player : Bedwars.getInstance().game.getAlive())
                        {
                            int colddown = getColddown(player);
                            if (isColddowning(player))
                            {
                                colddowns.put(player, colddown-1);
                                if (getUltimatesClass(player).colddown() != -1)
                                {
                                    player.player.setExp(((float) getColddown(player) / getUltimatesClass(player).colddown() / 20));
                                    int seconds = getColddown(player) / 20;
                                    player.player.setLevel(seconds);
                                }
                            }
                        }
                    }
                }.runTaskTimer(Bedwars.getInstance(), 0, 1);
            }
        }.runTaskLater(Bedwars.getInstance(), 10 * 20);
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, Bedwars.getInstance());
    }

    private void register(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, Bedwars.getInstance());
    }

    @Override
    public String[] startMessage() {
        return new String[] {"在商店里选择一项超能力！它们将会在10秒后启用！"};
    }

    @Override
    public DreamShop shop() {
        return new UltimatesShop();
    }
}