package me.faintcloudy.bedwars.game.dream.gun;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GunShop extends DreamShop {
    public GunShop() {
        super("枪械", new ItemBuilder(Material.WOOD_HOE).addFlag(ItemFlag.HIDE_ATTRIBUTES).build(), 5);
    }

    @Override
    public List<ClickableItem> shopItems(GamePlayer player, InventoryContents contents) {
        List<ClickableItem> shopItems = new ArrayList<>();
        for (int i = 0;i < GunShopItem.values().length;i++)
        {
            SlotPos slot = ItemShop.shopItemSlots.get(i);
            shopItems.add(GunShopItem.values()[i].toClickableItem(player, contents, null, slot));
        }

        return shopItems;
    }
}
