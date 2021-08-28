package me.faintcloudy.bedwars.game.resource;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ResourceType {
    IRON(Material.IRON_INGOT, Material.IRON_BLOCK, ChatColor.WHITE, "铁锭", 48),
    GOLD(Material.GOLD_INGOT, Material.GOLD_BLOCK, ChatColor.GOLD, "金锭", 12),
    DIAMOND(Material.DIAMOND, Material.DIAMOND_BLOCK, ChatColor.AQUA, "钻石", Arrays.asList(30, 24, 12), 8),
    EMERALD(Material.EMERALD, Material.EMERALD_BLOCK, ChatColor.DARK_GREEN, "绿宝石", Arrays.asList(56, 40, 28), 8),
    NONE(Material.AIR, Material.AIR, ChatColor.GRAY, "空！", 0);
    public Material material, block;
    public ChatColor color;
    public String cn;
    public int gatherAmount;
    public List<Integer> tierColddown = new ArrayList<>();

    ResourceType(Material material, Material block, ChatColor color, String cn, int gatherAmount)
    {
        this.material = material;
        this.block = block;
        this.color = color;
        this.cn = cn;
        this.gatherAmount = gatherAmount;
    }

    ResourceType(Material material, Material block, ChatColor color, String cn, List<Integer> tierColddown, int gatherAmount)
    {
        this.material = material;
        this.block = block;
        this.color = color;
        this.cn = cn;
        this.tierColddown.addAll(tierColddown);
        this.gatherAmount = gatherAmount;
    }

    public String display()
    {
        return color + cn;
    }

    public static ResourceType of(String name)
    {
        for (ResourceType type : values())
        {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }

        return NONE;
    }
}
