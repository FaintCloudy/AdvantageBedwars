package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.equipment.ArmorEquipment;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ArmorItem implements ShopItem {
    CHAINMAIL_ARMOR("永久的 锁链护甲", ArmorEquipment.CHAINMAIL_ARMOR, Price.of(40, ResourceType.IRON)),
    IRON_ARMOR("永久的 铁护甲", ArmorEquipment.IRON_ARMOR, Price.of(12, ResourceType.GOLD)),
    DIAMOND_ARMOR("永久的 钻石护甲", ArmorEquipment.DIAMOND_ARMOR, Price.of(6, ResourceType.EMERALD));


    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ArmorEquipment equipment;
    public static boolean enabled = true;

    ArmorItem(String displayName, ArmorEquipment equipment, Price price)
    {
        this.displayName = displayName;
        this.equipment = equipment;
        this.price = price;
    }

    ArmorItem(String displayName, ArmorEquipment equipment, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.equipment = equipment;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    ArmorItem(String displayName, ArmorEquipment equipment, Price price, String introduce)
    {
        this.displayName = displayName;
        this.equipment = equipment;
        this.price = price;
        this.introduces.add(introduce);
    }

    @Override
    public String insideName() {
        return this.name();
    }

    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(equipment.icon())
                .setDisplayName((enough ? "§a" : "§c") + displayName)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();
        lore.add(price.costDisplay());
        if (player.getTeam().upgradeLevels.get(TeamUpgrade.PROTECTION) > 0)
        {
            item = item.addGlow();
            lore.add("§7已升级: §e保护 " + ShopItem.toRome(player.getTeam().upgradeLevels.get(TeamUpgrade.PROTECTION)));
        }
        lore.add("");
        if (!introduces.isEmpty())
        {
            for (String s : introduces)
            {
                lore.add("§7" + s);
            }
            lore.add("");
        }
        if (type == ShopItemType.FAST_BUY)
        {
            lore.add("§bShift 加左键从快速购买中移除！");
        }
        else
        {
            lore.add("§bShift 加左键添加至快速购买！");
        }

        lore.add(unlocked(player) ? "§a已解锁" : (enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn));
        item.setLore(lore);

        return item.build();
    }

    @Override
    public boolean unlocked(GamePlayer player) {
        return player.armor == this.equipment;
    }

    @Override
    public ItemStack getItem(GamePlayer player) {
        equipment.set(player);

        return new ItemStack(Material.AIR);
    }

    @Override
    public Price price(GamePlayer player) {
        return price;
    }



}
