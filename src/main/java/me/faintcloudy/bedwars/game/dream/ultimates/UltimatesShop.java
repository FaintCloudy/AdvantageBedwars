package me.faintcloudy.bedwars.game.dream.ultimates;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class UltimatesShop extends DreamShop {
    public UltimatesShop() {
        super("超能力", new ItemStack(Material.BEACON), 8);
    }


    @Override
    public List<ClickableItem> shopItems(GamePlayer player, InventoryContents contents) {
        List<ClickableItem> shopItems = new ArrayList<>();
        for (UltimatesShopItem shopItem : UltimatesShopItem.values())
            shopItems.add(shopItem.toClickableItem(player));
        return shopItems;
    }
}
