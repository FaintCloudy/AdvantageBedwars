package me.faintcloudy.bedwars.game.shop;

import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum ShopItemType {
    FAST_BUY("快速购买", Material.NETHER_STAR),
    BLOCK("方块", new ItemBuilder(Material.STAINED_CLAY)
            .setDyeColor(DyeColor.BROWN).build()), MELEE("近战",Material.GOLD_SWORD),
    ARMOR("护甲", Material.CHAINMAIL_BOOTS), TOOL("工具", Material.STONE_PICKAXE),
    RANGED("远程", Material.BOW), POTION("药水", Material.POTION),
    SPECIAL("实用道具", Material.TNT);
    public String display;
    public ItemStack icon;
    ShopItemType(String display, Material icon)
    {
        this.display = display;
        this.icon = new ItemBuilder(icon).addFlag(ItemFlag.HIDE_ATTRIBUTES).build();
    }

    ShopItemType(String display, ItemStack icon)
    {
        this.display = display;
        this.icon = new ItemBuilder(icon).addFlag(ItemFlag.HIDE_ATTRIBUTES).build();
    }
}
