package me.faintcloudy.bedwars.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FastBuySettingsMenu {

    public static SmartInventory build()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(6, 9);
        builder.title("§7快速购买设置");
        builder.provider((player1, contents) -> {
            GamePlayer player = GamePlayer.get(player1);
            if (player.settingItem == null)
            {
                contents.inventory().close(player1);
                return;
            }

            ItemStack item = new ItemBuilder(player.settingItem.showItem(player, ShopItemType.FAST_BUY))
                    .setLore("", "§e正在添加到快捷物品购买栏！").build();

            contents.set(0, 4, ClickableItem.empty(item));
            ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.RED).setDisplayName("§c空闲槽位！")
                    .setLore("", "§e点击以添加").build();
            List<ShopItem> items = ItemShop.FAST_BUY.getShopItems(player);
            for (int i = 0; i< ItemShop.shopItemSlots.size(); i++)
            {
                SlotPos slot = ItemShop.shopItemSlots.get(i);
                contents.set(slot, ClickableItem.of(items.get(i) == null ? glassPane :
                        new ItemBuilder(items.get(i).showItem(player, ShopItemType.FAST_BUY))
                                .setLore("", "§e点击以替换").build(), event ->
                {
                    player.data.setQuickBuySettings(new MapBuilder<>(player.data.getQuickBuySettings())
                            .put(slot, player.settingItem).build());
                    player.settingItem = null;
                    ItemShop.FAST_BUY.open(player1);
                    player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                }));
            }
        });

        return builder.build();
    }
}
