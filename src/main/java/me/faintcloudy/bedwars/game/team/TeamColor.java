package me.faintcloudy.bedwars.game.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.material.Dye;

public enum TeamColor {
    NONE(ChatColor.GRAY, "", "", DyeColor.SILVER),
    BLUE(ChatColor.BLUE, "蓝队", "蓝", DyeColor.BLUE),
    RED(ChatColor.RED, "红队", "红", DyeColor.RED),
    GREEN(ChatColor.GREEN, "绿队", "绿", DyeColor.GREEN),
    YELLOW(ChatColor.YELLOW, "黄队", "黄", DyeColor.YELLOW);

    public ChatColor chatColor;
    public DyeColor dyeColor;
    public String cn, en;
    TeamColor(ChatColor chatColor, String cn, String en, DyeColor dyeColor)
    {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.cn = cn;
        this.en = en;
    }


    public static TeamColor of(String name)
    {
        for (TeamColor color : values()) {
            if (color.name().equalsIgnoreCase(name))
                return color;
        }

        return NONE;
    }
}
