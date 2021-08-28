//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.faintcloudy.bedwars.utils;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.util.Vector;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationUtils {
    public LocationUtils() {
    }

    public static Block getBedNeighbor(Block bed) {
        if (bed.getRelative(BlockFace.SOUTH).getType() == Material.BED)
            return bed.getRelative(BlockFace.SOUTH);
        else if (bed.getRelative(BlockFace.WEST).getType() == Material.BED)
            return bed.getRelative(BlockFace.WEST);
        else if (bed.getRelative(BlockFace.EAST).getType() == Material.BED)
            return bed.getRelative(BlockFace.EAST);
        return bed.getRelative(BlockFace.NORTH);
    }

    public static Location getLocation(Location location, double toX, double toY, double toZ) {
        return new Location(location.getWorld(), location.getX() + toX, location.getY() + toY, location.getZ() + toZ);
    }

    public static Location getStandardLocation(Location location)
    {

        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);

        return location;
    }

    public static Location getMiddleOfLocation(Location location)
    {
        Location locationClone = location.getBlock().getLocation().clone();
        if (locationClone.getX() >= 0)
            locationClone.setX(location.getX() + 0.5);
        else
            locationClone.setX(location.getX() - 0.5);

        if (locationClone.getZ() >= 0)
            locationClone.setZ(location.getZ() + 0.5);
        else
            locationClone.setZ(location.getZ() - 0.5);

        locationClone.setPitch(45.0F);
        locationClone.setYaw((int) locationClone.getYaw());
        return locationClone;
    }

    public static Location getClosest(Location origin, List<Location> locations) {
        Location closest = null;
        for (Location location : locations) {
            if (closest == null) {
                closest = location;
                continue;
            }

            if (location.distance(origin) < closest.distance(origin))
                closest = location;
        }

        return closest;
    }

    public static List<Location> getCircle(Location location, double radius, int points) {
        List<Location> locations = new ArrayList();
        double increment = 6.283185307179586D / (double) points;

        for (int i = 0; i < points; ++i) {
            double angle = (double) i * increment;
            double x = location.getX() + Math.cos(angle) * radius;
            double z = location.getZ() + Math.sin(angle) * radius;
            locations.add(new Location(location.getWorld(), x, location.getY(), z));
        }

        return locations;
    }

    public static List<Block> getSphere(Location location, int radius) {
        List<Block> blocks = new ArrayList();
        int X = location.getBlockX();
        int Y = location.getBlockY();
        int Z = location.getBlockZ();
        int radiusSquared = radius * radius;

        for (int x = X - radius; x <= X + radius; ++x) {
            for (int y = Y - radius; y <= Y + radius; ++y) {
                for (int z = Z - radius; z <= Z + radius; ++z) {
                    if ((X - x) * (X - x) + (Z - z) * (Z - z) <= radiusSquared) {
                        blocks.add(location.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }

        return blocks;
    }

    public static List<Block> getCube(Location location, int radius) {
        List<Block> blocks = new ArrayList();
        int X = location.getBlockX() - radius / 2;
        int Y = location.getBlockY() - radius / 2;
        int Z = location.getBlockZ() - radius / 2;

        for (int x = X; x < X + radius; ++x) {
            for (int y = Y; y < Y + radius; ++y) {
                for (int z = Z; z < Z + radius; ++z) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Vector getBackVector(Location location) {
        float f1 = (float) (location.getZ() + 1.0D * Math.sin(Math.toRadians((double) (location.getYaw() + 90.0F))));
        float f2 = (float) (location.getX() + 1.0D * Math.cos(Math.toRadians((double) (location.getYaw() + 90.0F))));
        return new Vector((double) f2 - location.getX(), 0.0D, (double) f1 - location.getZ());
    }

    public static boolean isSafeSpot(Location location) {
        Block blockCenter = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Block blockAbove = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
        Block blockBelow = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        if (!blockCenter.getType().isTransparent() && (!blockCenter.isLiquid() || blockCenter.getType().equals(Material.LAVA) || blockCenter.getType().equals(Material.STATIONARY_LAVA)) || !blockAbove.getType().isTransparent() && (!blockAbove.isLiquid() || blockAbove.getType().equals(Material.LAVA) || blockCenter.getType().equals(Material.STATIONARY_LAVA))) {
            return false;
        } else {
            return blockBelow.getType().isSolid() || blockBelow.getType().equals(Material.WATER) || blockBelow.getType().equals(Material.STATIONARY_WATER);
        }
    }

    public static boolean isInBorder(Location loc, Location loc2, int range) {
        int var4 = loc.getBlockX();
        int var5 = loc.getBlockZ();
        int var6 = loc2.getBlockX();
        int var7 = loc2.getBlockZ();
        return var6 < var4 + range && var7 < var5 + range && var6 > var4 - range && var7 > var5 - range;
    }

    public static double getDistance(Location loc1, Location loc2) {
        return Math.abs(loc1.getX() - loc2.getX()) + Math.abs(loc1.getY() - loc2.getY()) + Math.abs(loc1.getZ() - loc2.getZ());
    }

    public static boolean isInBorder(Location center, Location notCenter, double range) {
        double x = center.getX();
        double z = center.getZ();
        double x1 = (double)notCenter.getBlockX();
        double z1 = (double)notCenter.getBlockZ();
        return x1 < x + range && z1 < z + range && x1 > x - range && z1 > z - range;
    }

    public static Location getLocation(Location location, int x, int y, int z) {
        Location loc = location.getBlock().getLocation();
        loc.add((double)x, (double)y, (double)z);
        return loc;
    }

    public static Location getLocationYaw(Location location, double X, double Y, double Z) {
        double radians = Math.toRadians((double)location.getYaw());
        double x = Math.cos(radians) * X;
        double z = Math.sin(radians) * X;
        location.add(x, Y, z);
        location.setPitch(0.0F);
        return location;
    }

    public static Vector getPosition(Location location1, Location location2) {
        double X = location1.getX() - location2.getX();
        double Y = location1.getY() - location2.getY();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static Vector getPosition(Location location1, Location location2, double Y) {
        double X = location1.getX() - location2.getX();
        double Z = location1.getZ() - location2.getZ();
        return new Vector(X, Y, Z);
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (double)((player.getLocation().getYaw() - 90.0F) % 360.0F);
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if (0.0D <= rotation && rotation < 22.5D) {
            return "N";
        } else if (22.5D <= rotation && rotation < 67.5D) {
            return "NE";
        } else if (67.5D <= rotation && rotation < 112.5D) {
            return "E";
        } else if (112.5D <= rotation && rotation < 157.5D) {
            return "SE";
        } else if (157.5D <= rotation && rotation < 202.5D) {
            return "S";
        } else if (202.5D <= rotation && rotation < 247.5D) {
            return "SW";
        } else if (247.5D <= rotation && rotation < 292.5D) {
            return "W";
        } else if (292.5D <= rotation && rotation < 337.5D) {
            return "NW";
        } else {
            return 337.5D <= rotation && rotation < 360.0D ? "N" : null;
        }
    }

    public static List<Player> getNearbyPlayers(Location location, double radius) {
        List<Player> players = new ArrayList<>();

        for (Entity e : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (!CitizensAPI.getNPCRegistry().isNPC(e) && e instanceof Player && e.getLocation().distance(location) <= radius) {
                players.add((Player)e);
            }
        }

        return players;
    }

    public static BlockFace getDirection(Player player) {
        double rotation = (double)((player.getLocation().getYaw() - 90.0F) % 360.0F);
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if (0.0D <= rotation && rotation < 22.5D) {
            return BlockFace.SOUTH;
        } else if (22.5D <= rotation && rotation < 67.5D) {
            return BlockFace.SOUTH_WEST;
        } else if (67.5D <= rotation && rotation < 112.5D) {
            return BlockFace.NORTH;
        } else if (112.5D <= rotation && rotation < 157.5D) {
            return BlockFace.NORTH_WEST;
        } else if (157.5D <= rotation && rotation < 202.5D) {
            return BlockFace.WEST;
        } else if (202.5D <= rotation && rotation < 247.5D) {
            return BlockFace.NORTH_WEST;
        } else if (247.5D <= rotation && rotation < 292.5D) {
            return BlockFace.EAST;
        } else if (292.5D <= rotation && rotation < 337.5D) {
            return BlockFace.SOUTH_EAST;
        } else {
            return 337.5D <= rotation && rotation < 360.0D ? BlockFace.SOUTH : null;
        }
    }

    public static BlockFace reversal(String name) {
        name = name.toLowerCase();
        if (name.startsWith("north")) {
            return BlockFace.SOUTH;
        } else if (name.startsWith("south")) {
            return BlockFace.NORTH;
        } else if (name.startsWith("west")) {
            return BlockFace.EAST;
        } else {
            return name.startsWith("east") ? BlockFace.WEST : BlockFace.valueOf(name.toUpperCase());
        }
    }

    private static final BlockFace[] axis, radial;

    static {
        axis = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        radial = new BlockFace[]{BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    }

    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        return useSubCardinalDirections ? radial[Math.round(yaw / 45.0F) & 7] : axis[Math.round(yaw / 90.0F) & 3];
    }


}
