package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.rush.RushModeManager;
import me.faintcloudy.bedwars.listener.PlayerListener;
import me.faintcloudy.bedwars.utils.ActionBarUtil;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import me.faintcloudy.bedwars.utils.LocationUtils;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Builder implements UltimateClass {
    @Override
    public String displayName() {
        return "建筑师";
    }

    HashMap<GamePlayer, BuildMode> buildModes = new HashMap<>();
    static List<String> lore = Arrays.asList("§7轻松地建造桥梁和墙壁!", "被动地生成羊毛");

    public void swap(GamePlayer player) {
        BuildMode origin = buildModes.getOrDefault(player, BuildMode.BRIDGE);
        buildModes.put(player, origin == BuildMode.BRIDGE ? BuildMode.WALL : BuildMode.BRIDGE);
        BuildMode now = buildModes.get(player);
        ItemStack item = new ItemBuilder(Material.BRICK).setDisplayName("§a建筑师工具 §7- §e" + now.cn)
                .setLore(lore).build();
        player.player.setItemInHand(item);
        ActionBarUtil.sendActionBar(player.player, "§a已切换为§e" + now.cn + "§a模式！");
    }

    @EventHandler
    public void onSwap(PlayerInteractEvent event)
    {
        if (event.getMaterial() == Material.BRICK && event.getAction().name().contains("LEFT"))
        {
            GamePlayer player = GamePlayer.get(event.getPlayer());
            if (manager().getUltimatesClass(player) != this)
                return;
            this.swap(player);
        }
    }

    @Override
    public List<ItemStack> takeWith(GamePlayer player) {
        BuildMode now = get(player);
        ItemStack item = new ItemBuilder(Material.BRICK).setDisplayName("§a建筑师工具 §7- §e" + now.cn).build();
        return Collections.singletonList(item);
    }

    @Override
    public void disable(GamePlayer player) {
        player.player.getInventory().remove(Material.BRICK);
    }

    public BuildMode get(GamePlayer player) {
        return buildModes.getOrDefault(player, BuildMode.BRIDGE);
    }

    @Override
    public void init(GamePlayer player) {
        player.player.getInventory().remove(Material.BRICK);
        buildModes.put(player, BuildMode.BRIDGE);
        player.player.getInventory().addItem(new ItemBuilder(Material.BRICK).setDisplayName("§a建筑工具 §7- §e" + this.get(player).cn).build());
    }

    private int doProtectBed(Block block, int count, DyeColor woolColor) {
        List<Location> protects = new ArrayList<>();
        World world = block.getWorld();
        Arrays.asList(block, LocationUtils.getBedNeighbor(block)).forEach(location -> {
            protects.add(new Location(world, location.getX(), location.getY() + 1, location.getZ()));
            protects.add(new Location(world, location.getX() - 1, location.getY(), location.getZ()));
            protects.add(new Location(world, location.getX() + 1, location.getY(), location.getZ()));
            protects.add(new Location(world, location.getX(), location.getY(), location.getZ() + 1));
            protects.add(new Location(world, location.getX(), location.getY(), location.getZ() - 1));
        });
        int uses = 0;
        for (int i = 0; i < count; i++) {
            Block b = protects.get(i).getBlock();
            if (b.getType() != Material.AIR)
                continue;
            if (Bedwars.getInstance().game.region(b.getLocation()))
                continue;
            b.setType(Material.WOOL);
            b.setData(woolColor.getWoolData());
            PlayerListener.blocksPut.add(b);
            uses++;
        }

        return uses;
    }

    private int doBuildWall(Player player, Block block, int count, DyeColor woolColor)
    {
        Location loc = block.getLocation();
        Location x1 = loc.clone().add(1.0D, 0.0D, 0.0D);
        Location x2 = loc.clone().add(-1.0D, 0.0D, 0.0D);
        Location x3 = loc.clone().add(1.0D, 1.0D, 0.0D);
        Location x4 = loc.clone().add(-1.0D, 1.0D, 0.0D);
        Location x5 = loc.clone().add(1.0D, 2.0D, 0.0D);
        Location x6 = loc.clone().add(-1.0D, 2.0D, 0.0D);
        Location x7 = loc.clone().add(2.0D, 0.0D, 0.0D);
        Location x8 = loc.clone().add(-2.0D, 0.0D, 0.0D);
        Location x9 = loc.clone().add(2.0D, 1.0D, 0.0D);
        Location x10 = loc.clone().add(-2.0D, 1.0D, 0.0D);
        Location x11 = loc.clone().add(2.0D, 2.0D, 0.0D);
        Location x12 = loc.clone().add(-2.0D, 2.0D, 0.0D);
        Location z1 = loc.clone().add(0.0D, 0.0D, 1.0D);
        Location z2 = loc.clone().add(0.0D, 0.0D, -1.0D);
        Location z3 = loc.clone().add(0.0D, 1.0D, 1.0D);
        Location z4 = loc.clone().add(0.0D, 1.0D, -1.0D);
        Location z5 = loc.clone().add(0.0D, 2.0D, 1.0D);
        Location z6 = loc.clone().add(0.0D, 2.0D, -1.0D);
        Location z7 = loc.clone().add(0.0D, 0.0D, 2.0D);
        Location z8 = loc.clone().add(0.0D, 0.0D, -2.0D);
        Location z9 = loc.clone().add(0.0D, 1.0D, 2.0D);
        Location z10 = loc.clone().add(0.0D, 1.0D, -2.0D);
        Location z11 = loc.clone().add(0.0D, 2.0D, 2.0D);
        Location z12 = loc.clone().add(0.0D, 2.0D, -2.0D);
        Location y1 = loc.clone().add(0.0D, 1.0D, 0.0D);
        Location y2 = loc.clone().add(0.0D, 2.0D, 0.0D);

        List<Location> blocks = new ArrayList<>();
        if (!LocationUtils.yawToFace(player.getLocation().getYaw()).name().startsWith("EAST") && !LocationUtils.getDirection(player).name().startsWith("WEST")) {
            blocks.addAll(Arrays.asList(x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12, y1, y2, loc));
        }
        else
        {
            blocks.addAll(Arrays.asList(z1, z2, z3, z4, z5, z6, z7, z8, z9, z10, z11, z12, y1, y2, loc));
        }

        int uses = 0;

        for (int i = 0;i < count;i++)
        {
            Block b = blocks.get(i).getBlock();
            if (b.getType() != Material.AIR)
                continue;
            b.setType(Material.WOOL);
            b.setData(woolColor.getWoolData());
            PlayerListener.blocksPut.add(b);
            uses++;
        }



        return uses;

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event)
    {
        if (event.isCancelled())
            return;
        if (event.getBlock().getType() != Material.BRICK)
            return;
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (manager().getUltimatesClass(player) != this)
            return;

        event.setCancelled(true);
        event.setBuild(false);
        if (this.get(player) == BuildMode.WALL)
        {
            int wools = player.player.getInventory().all(Material.WOOL).size();
            int uses = this.doBuildWall(player.player, event.getBlock(), Math.min(wools, 15), player.getTeam().color.dyeColor);
            ItemUtils.take(player.player.getInventory(), Material.WOOL, uses);
            player.player.playSound(player.player.getLocation(), Sound.DIG_WOOL, 1, 1);
        }
        else
        {
            Vector vector = event.getBlockPlaced().getLocation().toVector().clone().subtract(event.getBlockAgainst().getLocation().toVector().clone());
            Block origin = event.getBlock();
            new BukkitRunnable()
            {
                Block block = origin;
                int places = 0;
                public void run()
                {
                    if (player.player.getInventory().all(Material.WOOL).size() <= 0)
                    {
                        cancel();
                        return;
                    }

                    places++;
                    if (Builder.this.blockAble(this.block))
                    {
                        this.block.setType(Material.WOOL);
                        this.block.setData(player.getTeam().color.dyeColor.getWoolData());
                        PlayerListener.blocksPut.add(this.block);
                        this.block.getWorld().playSound(this.block.getLocation(), Sound.DIG_WOOL, 1.0F, 1.0F);
                        ItemUtils.take(player.player.getInventory(), Material.WOOL, 1);
                    }
                    else
                    {
                        cancel();
                        return;
                    }
                    this.block = this.block.getLocation().add(vector).getBlock();

                    if (places >= 10)
                    {
                        cancel();
                    }

                }
            }.runTaskTimer(Bedwars.getInstance(), 3, 2L);

        }
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

    @EventHandler
    public void onProtectBed(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BED_BLOCK && event.getMaterial() == Material.BRICK && event.getPlayer().isSneaking())
        {
            GamePlayer player = GamePlayer.get(event.getPlayer());
            if (manager().getUltimatesClass(player) != this)
                return;
            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            int wools = player.player.getInventory().all(Material.WOOL).size();
            int uses = this.doProtectBed(event.getClickedBlock(), Math.min(wools, 8), player.getTeam().color.dyeColor);
            ItemUtils.take(player.player.getInventory(), Material.WOOL, uses);
            player.player.playSound(player.player.getLocation(), Sound.DIG_WOOL, 1, 1);
        }
    }

    enum BuildMode
    {
        BRIDGE("建桥"), WALL("建墙");
        public String cn;
        BuildMode(String cn)
        {
            this.cn = cn;
        }
    }

    @Override
    public int colddown() {
        return -1;
    }
}
