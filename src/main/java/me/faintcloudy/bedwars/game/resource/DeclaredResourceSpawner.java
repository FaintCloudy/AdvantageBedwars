package me.faintcloudy.bedwars.game.resource;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.holographic.SimpleHologram;
import me.faintcloudy.bedwars.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DeclaredResourceSpawner {
    public ResourceType type;
    public Location location;
    public int colddown;
    public SimpleHologram block, produce, resourceType, tier;
    public DeclaredResourceSpawner(ResourceType type, Location location)
    {
        this.type = type;
        this.location = LocationUtils.getStandardLocation(location);
        if (location.getBlock().getType().name().contains("SLAB"))
        {
            this.location = location.clone().add(0, 0.5, 0);
        }
        this.colddown = getDefaultColddown();
    }

    private int getTier()
    {
        return Bedwars.getInstance().game.tiers.get(type);
    }

    private void initHolograms()
    {
        Location location = this.location.clone();
        block = new SimpleHologram(location.add(0.0D, 1.25D, 0.0D), null);
        block.armorStand.setHelmet(new ItemStack(type.block));
        new BukkitRunnable()
        {
            public void run()
            {
                block.moveArmorStand();
            }
        }.runTaskTimer(Bedwars.getInstance(), 1L, 1L);
        produce = new SimpleHologram(location.add(0.0D, 0.55D, 0.0D), "§e在 §c" + colddown + " §e秒后产出");
        resourceType = new SimpleHologram(location.add(0.0D, 0.35D, 0.0D), type.color + "§l" + type.cn);
        tier = new SimpleHologram(location.add(0.0D, 0.35D, 0.0D), "§e等级 §c" + ShopItem.toRome(this.getTier()));
    }

    public void start()
    {
        initHolograms();
        new UpdateTask().runTaskTimer(Bedwars.getInstance(), 20L, 20L);
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

        Item item = location.getWorld().dropItem(location.clone().add(0.0D, 0.7D, 0.0D), new ItemStack(type.material));
        item.setVelocity(new Vector());
        item.setPickupDelay(5);
    }

    public int getDefaultColddown()
    {
        return type.tierColddown.get(getTier()-1);
    }

    class UpdateTask extends BukkitRunnable
    {
        @Override
        public void run() {
            colddown--;

            if (colddown <= 0)
            {
                colddown = getDefaultColddown();
                spawn();
            }

            produce.setTitle("§e在 §c" + colddown + " §e秒后产出");
            tier.setTitle("§e等级 §c" + ShopItem.toRome(getTier()));
        }

    }
}
