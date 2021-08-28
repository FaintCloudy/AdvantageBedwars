package me.faintcloudy.bedwars.game.dream.luckyblock;

import me.faintcloudy.bedwars.game.dream.DreamManager;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.SpecialItem;
import me.faintcloudy.bedwars.utils.EnumBuster;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockModeManager implements DreamManager {
    @Override
    public void init() {
        EnumBuster<SpecialItem> buster = new EnumBuster<>(SpecialItem.class, LuckyBlockModeManager.class, ItemShop.class);
        SpecialItem LUCKY_BLOCK = buster.make("LUCKY_BLOCK", SpecialItem.values().length,
                new Class[] { String.class, ItemStack.class, Price.class, String.class },
                new Object[] { "幸运方块", new ItemBuilder(Material.STAINED_GLASS, 3).setDisplayName("§6幸运方块")
                        .setDyeColor(DyeColor.YELLOW).build(), Price.of(1, ResourceType.EMERALD), "撸掉这个幸运方块你会随机获得一些东西!" });
        buster.addByValue(LUCKY_BLOCK);
    }

    @Override
    public String[] startMessage() {
        return new String[0];
    }
}
