package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.equipment.ToolEquipment;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ToolItem implements ShopItem {
    AXE(ToolEquipment.AXE, "该道具可升级。",
            "死亡将会导致损失一级！",
            "",
            "每次重生时，至少为最低等级。"),
    PICKAXE(ToolEquipment.PICKAXE, "该道具可升级。",
            "死亡将会导致损失一级！",
            "",
            "每次重生时，至少为最低等级。"),
    SHEARS(ToolEquipment.SHEARS, "适用于破坏羊毛，每次重生时会获得剪刀。");


    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    public ToolEquipment equipment;


    ToolItem(ToolEquipment equipment)
    {
        this.equipment = equipment;
    }

    ToolItem(ToolEquipment equipment, String... introduces)
    {
        this.equipment = equipment;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    ToolItem(ToolEquipment equipment, String introduce)
    {
        this.equipment = equipment;
        this.introduces.add(introduce);
    }

    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {
        boolean maxed = player.toolLevels.get(equipment) >= equipment.max;
        int level = maxed ? player.toolLevels.get(equipment) : player.toolLevels.get(equipment) + 1;
        Price price = equipment.costs.get(level);
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(equipment.items.get(level).clone())
                .setDisplayName((enough ? "§a" : "§c") + equipment.displays.get(level))
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();
        if (!maxed)
            lore.add(price.costDisplay());
        if (player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS) > 0 && equipment == ToolEquipment.AXE)
        {
            item = item.addGlow();
            lore.add("§7已升级: §e锋利 " + ShopItem.toRome(player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS)));
        }
        lore.add("§7等级: §e" + ShopItem.toRome(level));
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
        lore.add(maxed ? "§a已解锁" : (enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn));
        item.setLore(lore);

        return item.build();
    }

    @Override
    public String insideName() {
        return this.name();
    }

    @Override
    public ItemStack getItem(GamePlayer player) {
        boolean maxed = player.toolLevels.get(equipment) >= equipment.max;
        if (maxed)
        {
            return new ItemStack(Material.AIR);
        }
        int origin = player.toolLevels.get(equipment);
        player.toolLevels.put(equipment, origin + 1);
        equipment.set(player);
        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean unlocked(GamePlayer player) {
        return player.toolLevels.get(equipment) >= equipment.max;
    }

    @Override
    public Price price(GamePlayer player) {
        return equipment.costs.get(player.toolLevels.get(equipment) + 1);
    }



}
