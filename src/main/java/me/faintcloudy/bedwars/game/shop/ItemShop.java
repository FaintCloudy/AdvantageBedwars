package me.faintcloudy.bedwars.game.shop;

import com.google.gson.internal.$Gson$Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.game.shop.shopitem.*;
import me.faintcloudy.bedwars.listener.EventCaller;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public enum ItemShop {
    FAST_BUY(ShopItemType.FAST_BUY, Arrays.asList(null, null)),
    BLOCK_SHOP(ShopItemType.BLOCK, BlockItem.values()),
    MELEE_SHOP(ShopItemType.MELEE, MeleeItem.values()),
    ARMOR_SHOP(ShopItemType.ARMOR, ArmorItem.values()),
    TOOL_SHOP(ShopItemType.TOOL, ToolItem.values()),
    RANGED_SHOP(ShopItemType.RANGED, RangedItem.values()),
    POTION_SHOP(ShopItemType.POTION, PotionItem.values()),
    SPECIAL_SHOP(ShopItemType.SPECIAL, SpecialItem.values());
    public ShopItemType type;
    public List<ShopItem> shopItems;
    SmartInventory inventory;

    ItemShop(ShopItemType type, List<ShopItem> shopItems) {
        this.type = type;
        this.shopItems = shopItems;
        this.build();
    }

    ItemShop(ShopItemType type, ShopItem[] shopItems) {
        this.type = type;
        this.shopItems = Arrays.asList(shopItems);

        this.build();
    }

    public static List<SlotPos> shopItemSlots = new ArrayList<>();

    static {
        for (int row = 2; row <= 4; row++) {
            for (int column = 1; column < 8; column++) {
                shopItemSlots.add(SlotPos.of(row, column));
            }
        }
    }

    public List<ShopItem> getFastBuyItems(GamePlayer player) {

        List<ShopItem> callback = new ArrayList<>();
        for (SlotPos slot : shopItemSlots) {
            if (player.
                    data
                    .getQuickBuySettings()
                    .get(slot) != null && ShopItem.enabled.getOrDefault(player.data.getQuickBuySettings().get(slot), true)) {
                callback.add(player.data.getQuickBuySettings().get(slot));
                continue;
            }
            callback.add(null);
        }

        return callback;
    }

    public List<ShopItem> getShopItems(GamePlayer player) {
        if (this == FAST_BUY)
            return getFastBuyItems(player);
        List<ShopItem> newShopItems = new ArrayList<>();
        for (ShopItem shopItem : shopItems)
        {
            if (ShopItem.enabled.getOrDefault(shopItem, true))
                newShopItems.add(shopItem);
        }
        this.shopItems = newShopItems;
        return shopItems;
    }

    public void build() {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(6, 9);
        builder.title("??7" + type.display);
        builder.provider((entity, contents) -> {
            GamePlayer player = GamePlayer.get(entity);
            player.quickBuying = false;

            for (int i = 0; i < ItemShop.values().length; i++) {
                ItemShop itemShop = ItemShop.values()[i];
                ShopItemType type = itemShop.type;
                ItemBuilder item = new ItemBuilder(type.icon.clone())
                        .setDisplayName("??a" + type.display);
                if (type != this.type)
                    item.setLore("??e???????????????");
                contents.set(0, i, ClickableItem.of(item.build(), event ->
                {
                    if (type == this.type)
                        return;

                    itemShop.open(player.player);
                }));

                ItemBuilder glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .setDisplayName("??8??? ??7??????")
                        .setLore("??8??? ??????")
                        .setDyeColor(DyeColor.GRAY);
                if (type == this.type)
                    glassPane = glassPane.setDyeColor(DyeColor.GREEN);
                contents.set(1, i, ClickableItem.empty(glassPane.build()));


            }

            ClickableItem fastBuySlotGlassPane = ClickableItem.of(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.RED).setDisplayName("??c???????????????")
                    .setLore("??7??????????????????????????????????????? ??bShift???????????????",
                            "??7????????????????????????????????????").build(), event -> {
                player.player.sendMessage("??c?????????????????????????????????");
                player.player.sendMessage("??bShift????????????????????????????????????????????????????????????");
                player.player.playSound(player.player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            });

            for (int itemOrder = 0; itemOrder < this.getShopItems(player).size(); itemOrder++) {
                ShopItem shopItem = this.getShopItems(player).get(itemOrder);
                boolean fbs = shopItem == null;
                SlotPos slot = shopItemSlots.get(itemOrder);
                contents.set(shopItemSlots.get(itemOrder), fbs ? fastBuySlotGlassPane : shopItem.toClickableItem(player, contents, this.type, slot));
            }
            DreamShop dreamShop = Bedwars.getInstance().game.mode.manager.shop();
            contents.set(1, dreamShop.column, ClickableItem.empty(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .setDisplayName("??8??? ??7??????")
                            .setLore("??8??? ??7??????")
                            .setDyeColor(DyeColor.GRAY).build()
            ));

            if (dreamShop.icon != null && dreamShop.icon.getType() != Material.AIR)
                contents.set(0, dreamShop.column, ClickableItem.of(new ItemBuilder(dreamShop.icon.clone())
                        .setDisplayName("??a" + dreamShop.displayName).setLore("??e???????????????").build(), event ->
                        Bedwars.getInstance().game.mode.manager.shop().open(player.player)));
        });
        inventory = builder.build();
    }

    public static void openHotbarSettings(GamePlayer player, ShopItem item) {
        player.settingItem = item;
        Bedwars.getInstance().FAST_BUY_SETTINGS_MENU.open(player.player);
    }


    public void open(Player player) {
        inventory.open(player);
    }

    public static ItemShop getShop(ShopItemType type) {
        //TODO
        return null;
    }
}
