package me.faintcloudy.bedwars.game.dream.rush;

import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.events.GameStartedEvent;
import me.faintcloudy.bedwars.events.player.PlayerSpawnEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.events.GameEvent;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.MeleeItem;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.SpecialItem;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.listener.PlayerListener;
import me.faintcloudy.bedwars.utils.ActionBarUtil;
import me.faintcloudy.bedwars.utils.LocationUtils;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.lang.reflect.Field;
import java.util.*;

public class RushModeManager implements DreamManager, Listener {
    private final HashMap<GamePlayer, Boolean> buildingModes = new HashMap<>();

    public void changeBuildingMode(GamePlayer player)
    {
        boolean enable = !isEnabledBuildingMode(player);
        buildingModes.put(player, enable);
        ActionBarUtil.sendActionBar(player.player, enable ? "§a§l搭桥模式已开启" : "§c§l搭桥模式已关闭");
    }

    public boolean isEnabledBuildingMode(GamePlayer player)
    {
        return buildingModes.getOrDefault(player, false);
    }

    public HashMap<GamePlayer, Boolean> getBuildingModes() {
        return buildingModes;
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, Bedwars.getInstance());
    }

    @Override
    public String[] startMessage() {
        return new String[] {
                "               §e§l所有资源生成点自动满级！你的床有三层保护！手持羊毛并§e§l左",
                "                                 §e§l键点击羊毛开启建桥模式！"
        };
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event)
    {
        try {
            Field priceField = SpecialItem.BRIDGE_EGG.getClass().getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(SpecialItem.BRIDGE_EGG, Price.of(1, ResourceType.EMERALD));

            Field enderField = SpecialItem.ENDER_PEARL.getClass().getDeclaredField("price");
            enderField.setAccessible(true);
            enderField.set(SpecialItem.ENDER_PEARL, Price.of(2, ResourceType.EMERALD));

            Field diamondSwordField = MeleeItem.DIAMOND_SWORD.getClass().getDeclaredField("price");
            diamondSwordField.setAccessible(true);
            diamondSwordField.set(MeleeItem.DIAMOND_SWORD, Price.of(5, ResourceType.EMERALD));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        event.game.teams.forEach(team ->
        {
            team.upgradeLevels.put(TeamUpgrade.FORGE, TeamUpgrade.FORGE.maxLevel());
            team.upgradeLevels.put(TeamUpgrade.HASTE, 1);
            this.spawnProtect(team);
        });
        event.game.getAlive().forEach(p ->
        {
            p.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true);
            p.player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, false, false), true);
        });
        GameEvent.DIAMOND_RESOURCE_UPGRADE_III.onEvent();
        GameEvent.EMERALD_RESOURCE_UPGRADE_III.onEvent();
        event.game.gameEvents = Arrays.asList(GameEvent.BED_GONE, GameEvent.SUDDEN_DEATH, GameEvent.GAME_END);
    }

    @EventHandler
    public void onChangeBuildingMode(PlayerInteractEvent event)
    {
        if (event.getAction().name().contains("LEFT"))
        {
            if (event.getMaterial() == Material.WOOL)
            {
                changeBuildingMode(GamePlayer.get(event.getPlayer()));
            }
        }
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent event)
    {
        if (Bedwars.getInstance().game.state != GameState.GAMING)
            return;

        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (player.state != GamePlayer.PlayerState.ALIVE)
            return;

        if (event.isCancelled())
            return;
        if (event.getBlock().getType() != Material.WOOL)
            return;

        if (!(isEnabledBuildingMode(player)))
            return;

        Vector vector = event.getBlockPlaced().getLocation().toVector().clone().subtract(event.getBlockAgainst().getLocation().toVector().clone());
        Block origin = event.getBlock();
        new BukkitRunnable()
        {
            Block block = origin;
            int places = 0;
            public void run()
            {
                this.block = this.block.getLocation().add(vector).getBlock();
                places++;
                if (RushModeManager.this.blockAble(this.block))
                {
                    this.block.setType(Material.WOOL);
                    this.block.setData(origin.getData());
                    PlayerListener.blocksPut.add(this.block);
                    this.block.getWorld().playSound(this.block.getLocation(), Sound.DIG_WOOL, 1.0F, 1.0F);
                }
                else
                {
                    cancel();
                    return;
                }

                if (places >= 5)
                {
                    cancel();
                }

            }
        }.runTaskTimer(Bedwars.getInstance(), 3, 2L);

    }

    private boolean blockAble(Block block)
    {
        if (Bedwars.getInstance().game.region(block.getLocation()))
            return false;
        if (block.getType() != Material.AIR)
            return false;

        Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation().add(0.5D, 0.0D, 0.5D), 0.5D, 0.5D, 0.5D);
        for (Entity entity : entities)
        {
            if (entity instanceof Player)
            {
                if (Bedwars.getInstance().game.getAlive().contains(GamePlayer.get((Player) entity)))
                    return false;
            }
        }
        return true;
    }

    private void spawnProtect(Team team)
    {
        World world = Bedwars.getInstance().game.gameWorld;
        List<Location> layer1 = new ArrayList<>(); //木板
        List<Location> layer2 = new ArrayList<>(); //羊毛
        List<Location> layer3 = new ArrayList<>(); //玻璃
        Location bedBlock1 = team.bedLocation;
        Location bedBlock2 = LocationUtils.getBedNeighbor(team.bedLocation.getBlock()).getLocation();
        for (Location location : Arrays.asList(bedBlock1, bedBlock2))
        {
            layer1.add(new Location(world, location.getX(), location.getY() + 1, location.getZ()));
            layer1.add(new Location(world, location.getX()-1, location.getY(), location.getZ()));
            layer1.add(new Location(world, location.getX()+1, location.getY(), location.getZ()));
            layer1.add(new Location(world, location.getX(), location.getY(), location.getZ()+1));
            layer1.add(new Location(world, location.getX(), location.getY(), location.getZ()-1));
        }

        for (Location location : layer1)
        {
            layer2.add(new Location(world, location.getX(), location.getY() + 1, location.getZ()));
            layer2.add(new Location(world, location.getX()-1, location.getY(), location.getZ()));
            layer2.add(new Location(world, location.getX()+1, location.getY(), location.getZ()));
            layer2.add(new Location(world, location.getX(), location.getY(), location.getZ()+1));
            layer2.add(new Location(world, location.getX(), location.getY(), location.getZ()-1));
        }

        for (Location location : layer2)
        {
            layer3.add(new Location(world, location.getX(), location.getY() + 1, location.getZ()));
            layer3.add(new Location(world, location.getX()-1, location.getY(), location.getZ()));
            layer3.add(new Location(world, location.getX()+1, location.getY(), location.getZ()));
            layer3.add(new Location(world, location.getX(), location.getY(), location.getZ()+1));
            layer3.add(new Location(world, location.getX(), location.getY(), location.getZ()-1));
        }

        layer1.forEach(location ->
        {
            if (location.getBlock().getType() == Material.AIR)
            {
                location.getBlock().setType(Material.WOOD);
                PlayerListener.blocksPut.add(location.getBlock());
            }
        });
        layer2.forEach(location ->
        {
            if (location.getBlock().getType() == Material.AIR)
            {
                location.getBlock().setType(Material.WOOL);
                location.getBlock().setData(team.color.dyeColor.getWoolData());
                PlayerListener.blocksPut.add(location.getBlock());
            }
        });
        layer3.forEach(location ->
        {
            if (location.getBlock().getType() == Material.AIR)
            {
                location.getBlock().setType(Material.STAINED_GLASS);
                location.getBlock().setData(team.color.dyeColor.getData());
                PlayerListener.blocksPut.add(location.getBlock());
            }
        });
    }

    @EventHandler
    public void onSpawn(PlayerSpawnEvent event)
    {
        event.player.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true);
        event.player.player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, false, false), true);
    }
}
