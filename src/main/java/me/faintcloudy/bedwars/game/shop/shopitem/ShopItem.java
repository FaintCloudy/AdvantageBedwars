package me.faintcloudy.bedwars.game.shop.shopitem;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.listener.EventCaller;
import me.faintcloudy.bedwars.utils.ItemUtils;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public interface ShopItem {

    ItemStack showItem(GamePlayer player, ShopItemType type);
    ItemStack getItem(GamePlayer player);
    Price price(GamePlayer player);
    boolean unlocked(GamePlayer player);
    String insideName();

    static HashMap<String, ShopItem> nameToShopItemsHashMap()
    {
        List<ShopItem[]> items = Arrays.asList(ArmorItem.values(), BlockItem.values(),
                MeleeItem.values(), PotionItem.values(), RangedItem.values(), SpecialItem.values(),
                SpecialItem.values(), ToolItem.values());
        HashMap<String, ShopItem> nameToShopItemsHashMap = new HashMap<>();
        for (ShopItem[] shopItems : items)
        {
            for (ShopItem shopItem : shopItems)
                nameToShopItemsHashMap.put(shopItem.insideName(), shopItem);
        }

        return nameToShopItemsHashMap;
    }
    static boolean enoughPrice(GamePlayer player, Price price) {
        PlayerInventory inv = player.player.getInventory();
        int need = price.amount;
        for (ItemStack item : inv.getContents())
        {
            if (item == null)
                continue;
            if (item.getType() == price.resource.material)
            {
                need-=item.getAmount();
            }
        }

        return need <= 0;
    }


    static int need(GamePlayer player, Price price)
    {
        PlayerInventory inv = player.player.getInventory();
        int need = price.amount;
        for (ItemStack item : inv.getContents())
        {
            if (item == null)
                continue;
            if (item.getType() == price.resource.material)
            {
                need-=item.getAmount();
            }
        }

        return need;
    }

    HashMap<ShopItem, Boolean> enabled = new HashMap<>();

    static String toRome(int number) {
     StringBuilder rNumber = new StringBuilder();
       int[] aArray = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
      String[] rArray = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
      if (number < 1 || number > 3999) {
          rNumber = new StringBuilder("-1");
      } else {
          for (int i = 0; i < aArray.length; i++) {
              while (number >= aArray[i]) {
                      rNumber.append(rArray[i]);
              number -= aArray[i];
              }
          }
      }
      return rNumber.toString();
    }

    default ClickableItem toClickableItem(GamePlayer player, InventoryContents contents, ShopItemType type, SlotPos slot)
    {
        return ClickableItem.of(this.showItem(player, type).clone(), event ->
        {

            if (event.getClick() != ClickType.SHIFT_LEFT) {


                if (this.unlocked(player))
                    return;

                Price price = this.price(player);
                boolean enough = ShopItem.enoughPrice(player, price);
                if (!enough) {
                    player.sendMessage("§c不足! 还需要" + price.resource.cn + "x" + ShopItem.need(player, price) + "!");
                    player.player.playSound(player.player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    player.player.closeInventory();
                    return;
                }

                player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                String dp = this.showItem(player, type).getItemMeta().getDisplayName();
                StringBuilder display = new StringBuilder(dp).deleteCharAt(0).deleteCharAt(0);
                player.sendMessage("§a你购买了 §6" + display);
                ItemUtils.take(player.player.getInventory(), price.resource.material, price.amount);
                if (event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() >= 0) {
                    if (player.player.getInventory().getItem(event.getHotbarButton()) != null) {
                        ItemStack itemHotBarOn = player.player.getInventory().getItem(event.getHotbarButton()).clone();
                        player.player.getInventory().setItem(event.getHotbarButton(), this.getItem(player).clone());
                        player.player.getInventory().addItem(itemHotBarOn);
                    } else {
                        player.player.getInventory().setItem(event.getHotbarButton(), this.getItem(player).clone());
                    }
                } else {
                    if (this.getItem(player).clone().getType().name().contains("SWORD"))
                        player.setIfWoodSwordIn(this.getItem(player).clone());
                    else
                        player.player.getInventory().addItem(this.getItem(player).clone());
                }
                contents.inventory().open(player.player);
                EventCaller.callInventoryChangeEvent(player.player);
            } else {
                if (type == ShopItemType.FAST_BUY) {

                    player.data.setQuickBuySettings(new MapBuilder<>(player.data.getQuickBuySettings())
                            .remove(slot).build());
                    player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    contents.inventory().open(player.player);
                } else
                    ItemShop.openHotbarSettings(player, this);
            }


        });
    }
}
