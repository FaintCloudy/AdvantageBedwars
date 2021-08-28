package me.faintcloudy.bedwars.game.shop.shopitem;

import fr.minuskube.inv.ClickableItem;
import me.faintcloudy.bedwars.game.GamePlayer;

import java.util.List;

public interface ShopItemsCallback {

    List<ShopItem> getShopItems(GamePlayer player);
}
