package me.faintcloudy.bedwars.game.dream.gun;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.DreamShop;
import me.faintcloudy.bedwars.game.resource.ResourceType;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum GunShopItem implements ShopItem {
    MAGNUM_PISTOL(Gun.MAGNUM_PISTOL, Price.of(8, ResourceType.GOLD)),
    RIFLE(Gun.RIFLE, Price.of(8, ResourceType.GOLD)),
    SUB_MACHINE_GUN(Gun.SUB_MACHINE_GUN, Price.of(50, ResourceType.IRON)),
    FLAME_THROWER(Gun.FLAME_THROWER, Price.of(12, ResourceType.GOLD)),
    SHOT_GUU(Gun.SHOT_GUN, Price.of(1, ResourceType.EMERALD));
    public Gun gun;
    public Price price;
    GunShopItem(Gun gun, Price price)
    {
        this.gun = gun;
        this.price = price;
    }

    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(gun.icon)
                .setDisplayName((enough ? "§a" : "§c") + gun.displayName);
        List<String> lore = new ArrayList<>();
        lore.add(price.costDisplay());
        lore.add("");
        lore.add("§8● §7伤害: §a" + gun.damage);
        lore.add("§8● §7最大弹夹弹药: §a" + gun.clipAmount);
        lore.add("§8● §7射速: §a" + gun.shootingSpeed + "s");
        lore.add("§8● §7装弹: §a" + gun.loading + "s");
        lore.add("§8● §7最大射程: §a" + gun.range);
        lore.add("");

        if (type == ShopItemType.FAST_BUY)
        {
            lore.add("§bShift 加左键从快速购买中移除！");
        }
        else
        {
            lore.add("§bShift 加左键添加至快速购买！");
        }
        lore.add(enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn);
        item.setLore(lore);

        return item.build();
    }

    @Override
    public ItemStack getItem(GamePlayer player) {
        return gun.toItemStack();
    }



    @Override
    public Price price(GamePlayer player) {
        return price;
    }

    @Override
    public boolean unlocked(GamePlayer player) {
        return false;
    }

    @Override
    public String insideName() {
        return gun.name();
    }
}
