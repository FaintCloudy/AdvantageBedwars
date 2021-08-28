package me.faintcloudy.bedwars.game.dream.megawalls;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.events.player.PlayerDeadEvent;
import me.faintcloudy.bedwars.events.player.PlayerEnergyChangeEvent;
import me.faintcloudy.bedwars.events.player.PlayerSpawnEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.game.dream.megawalls.classes.ArmorSet;
import me.faintcloudy.bedwars.game.dream.megawalls.classes.MegaWallsClass;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.ArmorItem;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.game.shop.shopitem.SpecialItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MegaWallsModeManager implements DreamManager, Listener {

    static MegaWallsModeManager instance = null;
    {
        instance = this;
    }
    public static MegaWallsModeManager getInstance()
    {
        return instance;
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, Bedwars.getInstance());
        try {
            Field field = SpecialItem.GOLDEN_APPLE.getDeclaringClass().getDeclaredField("price");
            field.setAccessible(true);
            field.set(SpecialItem.GOLDEN_APPLE, Price.of(2, ResourceType.GOLD));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public boolean classesEnabled = false;

    public void setSelectedClasses(GamePlayer player, MegaWallsClass megaWallsClass)
    {
        selectedClasses.put(player, megaWallsClass);

        player.actionBarText = megaWallsClass.getActionBarText(player);
    }

    @Override
    public DreamShop shop() {
        return new ClassesShop();
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event)
    {

        new BukkitRunnable()
        {
            public void run()
            {
                classesEnabled = true;
                Bukkit.broadcastMessage("§a§l超级战墙职业已启用");
                MegaWallsClass.registerClasses();
                Bukkit.getPluginManager().registerEvents(new PlayerListener(), Bedwars.getInstance());
                Bukkit.getPluginManager().registerEvents(new EventCaller(), Bedwars.getInstance());
            }
        }.runTaskLater(Bedwars.getInstance(), 20 * 10);
        new BukkitRunnable()
        {
            public void run()
            {
                for (GamePlayer player : GamePlayer.getOnlineGamePlayers())
                {
                    if (player.state == GamePlayer.PlayerState.ALIVE)
                        player.actionBarText = getSelectedClass(player).getActionBarText(player);
                    else
                    {
                        if (player.actionBarText.contains("✔") || player.actionBarText.contains("✘"))
                            player.actionBarText = "";
                    }
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 10);
    }

    @EventHandler
    public void onSpawn(PlayerSpawnEvent event)
    {
        event.player.player.setMaxHealth(40);
        event.player.player.setHealth(40);
    }

    public HashMap<GamePlayer, MegaWallsClass> selectedClasses = new HashMap<>();
    public HashMap<GamePlayer, Integer> energies = new HashMap<>();
    public MegaWallsClass getSelectedClass(GamePlayer player)
    {
        return selectedClasses.getOrDefault(player, MegaWallsClass.ClassManager.HIM);
    }

    public void addEnergy(GamePlayer player, int energy)
    {
        setEnergy(player, energies.getOrDefault(player, 0) + energy);
    }

    public void setEnergy(GamePlayer player, int energy)
    {
        int origin = getEnergy(player);
        energy = Math.min(energy, 100);
        energies.put(player, energy);
        player.player.setLevel(energy);
        player.player.setExp(energy / 100f);
        Bukkit.getPluginManager().callEvent(new PlayerEnergyChangeEvent(player, origin, energy));
    }

    public void resetEnergy(GamePlayer player)
    {
        this.setEnergy(player, 0);
    }

    public int getEnergy(GamePlayer player)
    {
        return energies.getOrDefault(player, 0);
    }

    @Override
    public String[] startMessage() {
        return new String[]
                {
                        "在商店里选择一个职业！它们将会在10秒后启用！"
                };
    }
}
