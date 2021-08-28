package me.faintcloudy.bedwars.game.dream.gun;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Gun {
    PISTOL(Material.WOOD_HOE, 4, 12, 0.4, 1.5, 30, "手枪"),
    MAGNUM_PISTOL(Material.GOLD_HOE, 6, 6, 0.6, 3, 40, "马格南手枪"),
    RIFLE(Material.STONE_HOE, 4, 25, 0.2, 3, 40, "步枪"),
    SUB_MACHINE_GUN(Material.DIAMOND_HOE, 2, 45, 0.1, 2, 30, "冲锋枪"),
    FLAME_THROWER(Material.FLINT_AND_STEEL, 2, 50, 0.1, 3, 20, "火焰喷射器"),
    SHOT_GUN(Material.IRON_HOE, 2, 4, 1, 4, 10, "散弹枪");
    public Material icon;
    public int clipAmount;
    public double damage, shootingSpeed, loading, range;
    public String displayName;
    public HashMap<GamePlayer, Integer> amounts = new HashMap<>();
    public HashMap<GamePlayer, Integer> loadings = new HashMap<>();
    public HashMap<GamePlayer, Integer> shootingCD = new HashMap<>();
    Gun(Material icon, double damage, int clipAmount, double shootingSpeed, double loading, double range, String displayName)
    {
        this.icon = icon;
        this.damage = damage;
        this.clipAmount = clipAmount;
        this.shootingSpeed = shootingSpeed;
        this.loading = loading;
        this.range = range;
        this.displayName = displayName;
        if (Bedwars.getInstance().game.mode == BedwarsMode.GUN)
        new BukkitRunnable()
        {
            public void run()
            {
                if (Bedwars.getInstance().game.state != GameState.GAMING)
                    return;
                for (GamePlayer key : loadings.keySet())
                    loadings.put(key, loadings.getOrDefault(key, 0)-1);
                for (GamePlayer key : shootingCD.keySet())
                    shootingCD.put(key, shootingCD.getOrDefault(key, 0)-1);
                for (GamePlayer player : Bedwars.getInstance().game.getAlive())
                {
                    if (GunModeManager.getInstance().holdingGun(player.player))
                    {
                        player.player.setLevel(amounts.getOrDefault(player, clipAmount));
                        player.player.setExp(amounts.getOrDefault(player, clipAmount) / (float) clipAmount);
                    }
                    else
                    {
                        player.player.setLevel(0);
                        player.player.setExp(0);
                    }
                }
            }
        }.runTaskTimer(Bedwars.getInstance(), 0, 1);
    }

    public ItemStack toItemStack()
    {
        ItemBuilder item = new ItemBuilder(this.icon)
                .setDisplayName("§6" + this.displayName);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8● §7伤害: §a" + this.damage);
        lore.add("§8● §7最大弹夹弹药: §a" + this.clipAmount);
        lore.add("§8● §7射速: §a" + this.shootingSpeed + "s");
        lore.add("§8● §7装弹: §a" + this.loading + "s");
        lore.add("§8● §7最大射程: §a" + this.range);
        lore.add("");
        lore.add("§8hash=" + this.hashCode());
        item.setLore(lore);
        item.setUnbreakable(true);
        item.addFlag(ItemFlag.HIDE_UNBREAKABLE);
        item.addFlag(ItemFlag.HIDE_ATTRIBUTES);
        return item.build();
    }

    public static Gun getGun(int hashCode)
    {
        for (Gun gun : values())
        {
            if (gun.hashCode() == hashCode)
                return gun;
        }

        return null;
    }


}
