package me.faintcloudy.bedwars.listener;

import com.google.common.collect.Lists;

import com.sun.imageio.plugins.gif.GIFImageReader;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.TeamColor;
import me.faintcloudy.bedwars.stats.PlayerData;
import me.faintcloudy.bedwars.tasks.BridgeEggLaunchingTask;
import me.faintcloudy.bedwars.utils.ItemUtils;
import me.faintcloudy.bedwars.utils.ParticleEffects;
import net.citizensnpcs.api.CitizensAPI;
import net.minecraft.server.v1_8_R3.Explosion;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.WorldData;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.SmoothBrick;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.xml.crypto.dsig.Transform;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.*;

public class SpecialItemListener implements Listener {
    public SpecialItemListener() {
        new BukkitRunnable() {
            public void run() {
                for (GamePlayer key : milkTimes.keySet()) {
                    milkTimes.put(key, milkTimes.get(key) - 1);
                    if (milkTimes.get(key) <= 0) {
                        key.milkTime = false;
                        milkTimes.remove(key);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Bedwars.getInstance(), 0, 20);
        new BukkitRunnable() {
            public void run() {
                for (Entity entity : teamEntities) {
                    if (entity.isDead()) {
                        new BukkitRunnable() {
                            public void run() {
                                teamEntities.remove(entity);
                            }
                        }.runTask(Bedwars.getInstance());
                        continue;
                    }

                    if (entity.getMetadata("Team") == null || entity.getMetadata("Team").isEmpty())
                        continue;

                    MetadataValue meta = entity.getMetadata("Team").get(0);
                    if (meta.asString().isEmpty())
                        continue;

                    TeamColor color = TeamColor.valueOf(meta.asString());
                    GamePlayer nearest = null;


                    for (GamePlayer player : GamePlayer.getOnlineGamePlayers()) {
                        if (player.state != GamePlayer.PlayerState.ALIVE)
                            continue;
                        if (color == player.getTeam().color)
                            continue;
                        if (nearest == null) {
                            nearest = player;
                            continue;
                        }
                        if (player.player.getLocation().distance(entity.getLocation()) < nearest.player.getLocation().distance(entity.getLocation()))
                            nearest = player;
                    }

                    if (nearest == null)
                        continue;
                    Creature creature = (Creature) entity;
                    if (nearest.player.getLocation().distance(entity.getLocation()) <= 10) {
                        creature.setTarget(nearest.player);

                    } else {
                        creature.setTarget(null);
                    }
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 14);
    }

    @EventHandler
    public void onPlaceTNT(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        if (event.getBlock().getType() == Material.TNT) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            ItemUtils.consumeItemInHand(event.getPlayer());

            Location location = event.getBlock().getLocation().clone();
            TNTPrimed tnt = location.getWorld().spawn(location.clone().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
            tnt.setYield(3.0F);
            tnt.setIsIncendiary(false);
            tnt.setFuseTicks(50);
            tnt.setMetadata("TNTLaunch", new FixedMetadataValue(Bedwars.getInstance(), event.getPlayer().getName()));
            tnt.setCustomName(event.getPlayer().getName());
        }
    }

    public static Set<Player> noFallDamagePlayers = new HashSet<>();

    @EventHandler
    public void onNoFall(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
            {
                Player player = (Player) event.getEntity();
                if (noFallDamagePlayers.contains(player))
                {
                    event.setCancelled(true);
                    noFallDamagePlayers.remove(player);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTNTDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager.hasMetadata("TNTLaunch")) {
            Entity entity = e.getEntity();
            if (CitizensAPI.getNPCRegistry().isNPC(entity))
                return;
            if (entity instanceof Player) {
                Game game = Bedwars.getInstance().game;
                GamePlayer player = GamePlayer.get((Player) entity);
                if (damager instanceof TNTPrimed) {

                    if (player.state != GamePlayer.PlayerState.ALIVE) {
                        return;
                    }

                    if (game.state == GameState.GAMING) {
                        e.setCancelled(false);

                        e.setDamage(3);
                        player.player.setVelocity(player.player.getLocation().toVector().clone().subtract(damager.getLocation().toVector()).setY(1));
                        noFallDamagePlayers.add(player.player);

                    }
                }

            }
        }
    }

    HashMap<GamePlayer, Integer> milkTimes = new HashMap<>();
    List<GamePlayer> fireBallColddown = new ArrayList<>();


    List<Entity> teamEntities = new ArrayList<>();


    @EventHandler
    public void onBridgeEgg(ProjectileLaunchEvent event) {
        if (event.getEntity().getType() != EntityType.EGG || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        new BridgeEggLaunchingTask(GamePlayer.get(player), event.getEntity()).runTaskTimer(Bedwars.getInstance(), 1L, 1L);
    }

    @EventHandler
    public void onIronGolemDamagePlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof IronGolem)
            event.setDamage(10.0D);
    }

    @EventHandler
    public void onSpawnSliverFish(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
            GamePlayer player = GamePlayer.get((Player) event.getEntity().getShooter());
            Snowball snowball = (Snowball) event.getEntity();

            Silverfish silverfish = snowball.getWorld().spawn(snowball.getLocation().clone().add(0.5, 0, 0.5), Silverfish.class);
            silverfish.setMaxHealth(40);
            silverfish.setHealth(40);
            silverfish.setCustomName(player.getTeam().color + player.getTeam().color.en + " 蠹虫");
            silverfish.setCustomNameVisible(true);
            silverfish.setMetadata("Team", new FixedMetadataValue(Bedwars.getInstance(), player.getTeam().color.name()));
            teamEntities.add(silverfish);
            new BukkitRunnable() {
                int count = 30;

                public void run() {
                    count--;
                    if (count <= 0) {
                        silverfish.remove();
                        cancel();
                        return;
                    }
                    silverfish.setCustomName(player.getTeam().color.chatColor + player.getTeam().color.en + " 蠹虫 " + count + "s");
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 20);

        }
    }

    @EventHandler
    public void onSpawnIronGolem(PlayerInteractEvent event) {
        if (event.getMaterial() == Material.MONSTER_EGG && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = event.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5);
            switch (event.getBlockFace()) {
                case UP:
                    loc.setY(loc.getY() + 1.0D);
                    break;
                case DOWN:
                    loc.setY(loc.getY() - 2.0D);
                    break;
                case WEST:
                    loc.setX(loc.getX() - 1.5D);
                    break;
                case SOUTH:
                    loc.setZ(loc.getZ() - 1.5D);
                    break;
                case NORTH:
                    loc.setX(loc.getX() + 1.5D);
                    break;
                case EAST:
                    loc.setZ(loc.getZ() + 1.5D);
            }

            ItemUtils.consumeItemInHand(event.getPlayer());
            IronGolem ironGolem = loc.getWorld().spawn(loc, IronGolem.class);
            GamePlayer player = GamePlayer.get(event.getPlayer());
            ironGolem.setMaxHealth(40);
            ironGolem.setHealth(40);
            ironGolem.setCustomName(player.getTeam().color.chatColor + player.getTeam().color.en + " 梦境守护者");
            ironGolem.setCustomNameVisible(true);
            ironGolem.setMetadata("Team", new FixedMetadataValue(Bedwars.getInstance(), player.getTeam().color.name()));
            teamEntities.add(ironGolem);
            new BukkitRunnable() {
                int count = 120;

                public void run() {
                    count--;
                    if (count <= 0) {
                        ironGolem.remove();
                        cancel();
                        return;
                    }
                    ironGolem.setCustomName(player.getTeam().color.chatColor + player.getTeam().color.en + " 梦境守护者 " + count + "s");
                }
            }.runTaskTimer(Bedwars.getInstance(), 0, 20);

        }
    }


    @EventHandler
    public void onMilkBucket(PlayerBucketEmptyEvent event) {
        if (event.isCancelled())
            return;
        if (event.getBucket() == Material.MILK_BUCKET) {
            GamePlayer player = GamePlayer.get(event.getPlayer());
            player.milkTime = true;
            milkTimes.put(player, 30);
        }
    }

    @EventHandler
    public void onSponge(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        if (event.getBlock().getType() != Material.SPONGE)
            return;
        if (Bedwars.getInstance().game.region(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
        event.getBlock().setType(Material.AIR);

        new BukkitRunnable() {
            public void run() {
                Block block = event.getBlock();
                new BukkitRunnable() {
                    public void run() {
                        for (Location location : getAround(block.getLocation(), 1.2)) {
                            ParticleEffects.CLOUD.display(new Vector(), 1000F, location, 50);
                            if (location.getBlock().isLiquid())
                                location.getBlock().setType(Material.AIR);
                        }
                    }
                }.runTask(Bedwars.getInstance());
                new BukkitRunnable() {
                    public void run() {
                        for (Location location : getAround(block.getLocation(), 2.4)) {
                            ParticleEffects.CLOUD.display(new Vector(), 1000F, location, 50);
                            if (location.getBlock().isLiquid())
                                location.getBlock().setType(Material.AIR);
                        }
                    }
                }.runTaskLater(Bedwars.getInstance(), 10);
                new BukkitRunnable() {
                    public void run() {
                        for (Location location : getAround(block.getLocation(), 3.6)) {
                            ParticleEffects.CLOUD.display(new Vector(), 1000F, location, 50);
                            if (location.getBlock().isLiquid())
                                location.getBlock().setType(Material.AIR);
                        }
                    }
                }.runTaskLater(Bedwars.getInstance(), 10);

                for (int i = 1; i <= 2; i++) {
                    new BukkitRunnable() {
                        public void run() {
                            for (Location location : getAround(block.getLocation(), 2.4))
                                ParticleEffects.CLOUD.display(new Vector(), 1000.0F, location, 50.0D);
                        }
                    }.runTaskLater(Bedwars.getInstance(), 20 + 10 * i);
                }
            }


            private List<Location> getAround(Location origin, double radius) {
                List<Location> locations = new ArrayList<>();
                Location min = new Location(origin.getWorld(), origin.getX() - radius, origin.getY() - radius, origin.getZ() - radius);
                Location max = new Location(origin.getWorld(), origin.getX() + radius, origin.getY() + radius, origin.getZ() + radius);
                double x;
                for (x = min.getX(); x <= max.getX(); x += 0.6D) {
                    double y;
                    for (y = min.getY(); y <= max.getY(); y += 0.6D) {
                        double z;
                        for (z = min.getZ(); z <= max.getZ(); z += 0.6D) {
                            locations.add(new Location(origin.getWorld(), x, y, z));
                        }
                    }
                }
                return locations;
            }
        }.runTaskAsynchronously(Bedwars.getInstance());
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onInteractFireball(PlayerInteractEvent e) {
        GamePlayer player = GamePlayer.get(e.getPlayer());
        Game game = Bedwars.getInstance().game;
        if (e.getItem() != null && game != null) {
            if (game.state == GameState.GAMING && (e.getAction().equals(Action.RIGHT_CLICK_AIR)
                    || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getItem().getType() == (new ItemStack(Material.FIREBALL)).getType()) {
                if (fireBallColddown.contains(player)) {
                    e.setCancelled(true);
                    player.sendMessage("§c冷却中... 0.5s");
                } else {
                    this.fireBallColddown.add(player);
                    Fireball fireball = player.player.launchProjectile(Fireball.class);
                    fireball.setYield(3.0F);
                    fireball.setBounce(false);
                    fireball.setShooter(player.player);
                    fireball.setIsIncendiary(false);
                    fireball.setMetadata("FireBall", new FixedMetadataValue(Bedwars.getInstance(), player.player.getName()));
                    ItemUtils.consumeItemInHand(player.player);
                    new BukkitRunnable() {
                        public void run() {
                            if (fireball.isOnGround() || fireball.isDead()) {

                                for (GamePlayer p : GamePlayer.getOnlineGamePlayers()) {
                                    Game game = Bedwars.getInstance().game;
                                    if (game != null && game.state == GameState.GAMING && p.state == GamePlayer.PlayerState.ALIVE
                                            && p.player.getGameMode() != GameMode.CREATIVE && fireball.getWorld() == p.player.getWorld()
                                            && p.player.getLocation().distance(fireball.getLocation()) <= 4.0D) {
                                        p.player.damage(3, fireball);

                                        p.player.setVelocity(player.player.getLocation().toVector().clone().subtract(fireball.getLocation().toVector().clone()).setY(1));
                                        noFallDamagePlayers.add(p.player);

                                    }
                                }
                                cancel();
                            }
                        }
                    }.runTaskTimer(Bedwars.getInstance(), 0, 1);
                    new BukkitRunnable() {
                        public void run() {
                            fireBallColddown.remove(player);
                        }
                    }.runTaskLater(Bedwars.getInstance(), 10);
                    e.setCancelled(true);
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFireballDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (damager.hasMetadata("FireBall")) {
            if (entity instanceof Player && damager instanceof Fireball) {
                Player player = (Player) entity;
                Game game = Bedwars.getInstance().game;
                if (game.state == GameState.GAMING && GamePlayer.get(player).state == GamePlayer.PlayerState.ALIVE) {
                    e.setDamage(3);
                }

            }
        }
    }


    /*private static final BlockFace[] HORIZONTALS = new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    private final List<BlockOperation> zeroHolder = loadAndRotate(0);
    private final List<BlockOperation> ninetyHolder = loadAndRotate(90);
    private final List<BlockOperation> hundreadeightyHolder = loadAndRotate(180);
    private final List<BlockOperation> twoseventyHolder = loadAndRotate(270);


    public static BlockFace getHorizontal(int p_176731_0_) {
        return HORIZONTALS[MathHelper.a(p_176731_0_ % HORIZONTALS.length)];
    }

    public List<BlockOperation> loadAndRotate(int rotation) {
        List<BlockOperation> ladderopreations = Lists.newArrayList();
        List<BlockOperation> operations = Lists.newArrayList();
        try {
            BukkitWorld bukkitWorld = new BukkitWorld(Bukkit.getWorld("world"));
            WorldData pasteWorldData = bukkitWorld.getWorldData();
            Clipboard clipboard = ClipboardFormat.SCHEMATIC.load(Bedwars.getInstance().getResource("schematics/popup.schematic")).getClipboard();


            ClipboardHolder holder = new ClipboardHolder(clipboard, pasteWorldData);
            clipboard.setOrigin(clipboard.getRegion().getCenter());
            AffineTransform transform = new AffineTransform();
            transform = transform.rotateY(-rotation);
            holder.setTransform(holder.getTransform().combine((Transform) transform));
            int rotational = (int) (rotation / 90.0D);
            int lx = 0;
            int lz = 0;
            for (int y = 0; y < clipboard.getRegion().getHeight(); y++) {
                for (int x = 0; x < clipboard.getRegion().getWidth(); x++) {
                    for (int z = 0; z < clipboard.getRegion().getLength(); z++) {
                        BaseBlock block = clipboard.getBlock(new Vector(clipboard.getRegion().getMinimumPoint().getBlockX() + x, clipboard.getRegion().getMinimumPoint().getBlockY() + y, clipboard.getRegion().getMinimumPoint().getBlockZ() + z));
                        if (block.getId() != 0) {
                            Vector mine = new Vector(x, y, z);
                            Vector newv = holder.getTransform().apply(mine);
                            if (block.getId() == 65) {
                                for (int i = 0; i < rotational; i++) {
                                    block = new BaseBlock(block.getId(), BlockData.rotate90(block.getId(), block.getData()));
                                }
                                lx = newv.getBlockX();
                                lz = newv.getBlockZ();
                                BlockOperation operation = new BlockOperation(Material.getMaterial(block.getId()), (byte) block.getData(), newv.getBlockX(), newv.getBlockY(), newv.getBlockZ());
                                ladderopreations.add(operation);
                            } else {

                                BlockOperation operation = new BlockOperation(Material.getMaterial(block.getId()), (byte) block.getData(), newv.getBlockX(), newv.getBlockY(), newv.getBlockZ());
                                operations.add(operation);
                            }
                        }
                    }
                }
            }

            operations.addAll(ladderopreations);
            for (BlockOperation op : operations) {
                BlockOperation blockOperation1 = op;
                blockOperation1.x = blockOperation1.x - lx;
                blockOperation1 = op;
                blockOperation1.z = blockOperation1.z - lz;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operations;
    }

    public List<BlockOperation> getOperationFromYaw(float yaw) {
        BlockFace face = getHorizontal(MathHelper.floor((yaw * 4.0F / 360.0F) + 0.5D) & 0x3);
        if (face == BlockFace.NORTH)
            return this.ninetyHolder;
        if (face == BlockFace.EAST)
            return this.hundreadeightyHolder;
        if (face == BlockFace.SOUTH)
            return this.twoseventyHolder;
        if (face == BlockFace.WEST) {
            return this.zeroHolder;
        }
        return null;
    }


    public static class BlockOperation {
        private final Material mat;
        private final byte data;
        private final int y;
        private int x;
        private int z;

        public BlockOperation(Material mat, byte data, int x, int y, int z) {
            this.mat = mat;
            this.data = data;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }*/

}
