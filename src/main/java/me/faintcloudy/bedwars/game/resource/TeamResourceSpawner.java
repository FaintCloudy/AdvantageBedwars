package me.faintcloudy.bedwars.game.resource;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GameScale;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TeamResourceSpawner {
    public ResourceType type;
    public Location location;
    public int colddown;
    public Team team;
    public TeamResourceSpawner(ResourceType type, Location location, Team team)
    {
        this.type = type;
        this.location = location;
        if (location.getBlock().getType().name().contains("SLAB"))
        {
            this.location = location.clone().add(0, 0.5, 0);
        }
        this.colddown = -1;
        this.team = team;
    }


    public void start()
    {
        this.colddown = getDefaultColddown();
        new UpdateTask().runTaskLater(Bedwars.getInstance(), getDefaultColddown());
    }

    class UpdateTask extends BukkitRunnable
    {
        @Override
        public void run() {
            spawn();
            new UpdateTask().runTaskLater(Bedwars.getInstance(), getDefaultColddown());
        }
    }

    public void spawn()
    {
        int itemsAround = 0;
        for (Entity item : location.getWorld().getNearbyEntities(location, 5.0D, 3.0D, 5.0D))
        {
            if (item instanceof Item && ((Item) item).getItemStack().getType() == type.material)
            {
                itemsAround += ((Item) item).getItemStack().getAmount();
            }
        }



        if (itemsAround >= type.gatherAmount)
            return;

        Item item = location.getWorld().dropItem(location.clone().add(0, 1, 0), new ItemStack(type.material));
        item.setVelocity(new Vector());
        item.setPickupDelay(5);
    }

    public int getDefaultColddown()
    {
        return this.getColddownTicks().get(type);
    }

    public Map<ResourceType, Integer> getColddownTicks()
    {
        Map<ResourceType, Integer> colddownTicks = new HashMap<>();
        GameScale scale = team.game.scale;
        int forgeLevel = team.upgradeLevels.get(TeamUpgrade.FORGE);
        if (scale.ppt > 2)
        {
            switch (forgeLevel)
            {
                case 0:
                    colddownTicks.put(ResourceType.IRON, 20);
                    colddownTicks.put(ResourceType.GOLD, 100);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 1:
                    colddownTicks.put(ResourceType.IRON, 18);
                    colddownTicks.put(ResourceType.GOLD, 80);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 2:
                    colddownTicks.put(ResourceType.IRON, 15);
                    colddownTicks.put(ResourceType.GOLD, 70);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 3:
                    colddownTicks.put(ResourceType.IRON, 13);
                    colddownTicks.put(ResourceType.GOLD, 60);
                    colddownTicks.put(ResourceType.EMERALD, 600);
                case 4:
                    colddownTicks.put(ResourceType.IRON, 10);
                    colddownTicks.put(ResourceType.GOLD, 40);
                    colddownTicks.put(ResourceType.EMERALD, 400);
            }
        }
        else
        {
            switch (forgeLevel)
            {
                case 0:
                    colddownTicks.put(ResourceType.IRON, 40);
                    colddownTicks.put(ResourceType.GOLD, 160);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 1:
                    colddownTicks.put(ResourceType.IRON, 35);
                    colddownTicks.put(ResourceType.GOLD, 140);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 2:
                    colddownTicks.put(ResourceType.IRON, 30);
                    colddownTicks.put(ResourceType.GOLD, 130);
                    colddownTicks.put(ResourceType.EMERALD, -1);
                case 3:
                    colddownTicks.put(ResourceType.IRON, 20);
                    colddownTicks.put(ResourceType.GOLD, 120);
                    colddownTicks.put(ResourceType.EMERALD, 600);
                case 4:
                    colddownTicks.put(ResourceType.IRON, 15);
                    colddownTicks.put(ResourceType.GOLD, 110);
                    colddownTicks.put(ResourceType.EMERALD, 400);
            }
        }

        return colddownTicks;
    }

}
