package me.faintcloudy.bedwars.tasks;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.listener.PlayerListener;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class BridgeEggLaunchingTask extends BukkitRunnable {
    public Egg egg;
    public GamePlayer launcher;
    public final Location locationLaunched;
    public BridgeEggLaunchingTask(GamePlayer launcher, Entity egg)
    {
        this.egg = (Egg) egg;
        this.launcher = launcher;
        this.locationLaunched = launcher.player.getLocation().clone();
    }

    @Override
    public void run() {
        Location origin = egg.getLocation();
        if (egg.isDead() || locationLaunched.getY() - egg.getLocation().getY() > 8 || locationLaunched.distance(egg.getLocation()) > 30)
        {
            cancel();
            egg.remove();
        }

        if (locationLaunched.distance(egg.getLocation()) >= 3)
        {

            Block blockSpawn1 = origin.clone().subtract(0, 2, 0).getBlock();
            Block blockSpawn2 = origin.clone().subtract(1, 2, 0).getBlock();
            Block blockSpawn3 = origin.clone().subtract(0, 2, 1).getBlock();
            for (Block block : Arrays.asList(blockSpawn1, blockSpawn2, blockSpawn3))
            {
                if (Bedwars.getInstance().game.region(block.getLocation()))
                    continue;

                if (block.getType() != Material.AIR)
                    continue;

                PlayerListener.blocksPut.add(block);

                block.setType(Material.WOOL);
                block.setData(launcher.getTeam().color.dyeColor.getWoolData());
            }
        }

    }
}
