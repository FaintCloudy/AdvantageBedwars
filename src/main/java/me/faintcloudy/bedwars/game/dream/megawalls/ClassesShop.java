package me.faintcloudy.bedwars.game.dream.megawalls;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClassesShop extends DreamShop {
    public ClassesShop() {
        super("超级战墙职业", new ItemStack(Material.SOUL_SAND), 8);
    }

    @Override
    public List<ClickableItem> shopItems(GamePlayer player, InventoryContents contents) {
        List<ClickableItem> shopItems = new ArrayList<>();
        for (ClassShopItem shopItem : ClassShopItem.values())
            shopItems.add(shopItem.toClickableItem(player));
        return shopItems;
    }
}
