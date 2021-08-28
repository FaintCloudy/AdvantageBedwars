package me.faintcloudy.bedwars.game.dream;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.listener.EventCaller;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class DreamShop {
    public String displayName;
    public ItemStack icon;
    private SmartInventory inventory = null;
    public int column;

    public DreamShop(String displayName, ItemStack icon, int column)
    {
        this.displayName = displayName;
        this.icon = icon;
        this.build();
        this.column = column;
    }

    public abstract List<ClickableItem> shopItems(GamePlayer player, InventoryContents contents);

    public void open(Player player)
    {
        if (inventory == null)
            this.build();
        inventory.open(player);
    }

    public void build()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(6, 9);
        builder.title("§8" + displayName);
        builder.provider((entity, contents) -> {
            GamePlayer player = GamePlayer.get(entity);
            player.quickBuying = false;

            for (int i = 0; i<ItemShop.values().length; i++)
            {
                ItemShop itemShop = ItemShop.values()[i];
                ShopItemType type = itemShop.type;
                ItemBuilder item = new ItemBuilder(type.icon.clone())
                        .setDisplayName("§a" + type.display);
                item.setLore("§e点击查看！");
                contents.set(0, i, ClickableItem.of(item.build(), event ->
                {
                    itemShop.open(player.player);
                }));

                ItemBuilder glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName("§8⇧ §7类别")
                        .setLore("§8⇩ 物品")
                        .setDyeColor(DyeColor.GRAY);
                //if (type == this.type)
                    //glassPane = glassPane.setDyeColor(DyeColor.GREEN);
                contents.set(1, i, ClickableItem.empty(glassPane.build()));
            }

            for (int itemOrder = 0;itemOrder<this.shopItems(player, contents).size();itemOrder++)
            {
                ClickableItem shopItem = this.shopItems(player, contents).get(itemOrder);
                SlotPos slot = ItemShop.shopItemSlots.get(itemOrder);
                contents.set(slot, shopItem);
            }

            contents.set(0, column, ClickableItem.empty(new ItemBuilder(icon).setDisplayName("§a" + displayName).build()));
            contents.set(1, column, ClickableItem.empty(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .setDisplayName("§8⇧ §7类别")
                            .setLore("§8⇩ §7物品")
                            .setDyeColor(DyeColor.GREEN).build()
            ));
        });
        inventory = builder.build();
    }
}
