package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum BlockItem implements ShopItem {
    WOOL("羊毛", new ItemStack(Material.WOOL, 16), Price.of(4, ResourceType.IRON),
            "可用于搭桥穿越岛屿。搭出的桥的颜色会对应你的队伍颜色。"),
    CLAY("粘土", new ItemStack(Material.STAINED_CLAY, 16), Price.of(12, ResourceType.IRON),
            "用于保卫床的基础方块。"),
    GLASS("防爆玻璃", new ItemStack(Material.STAINED_GLASS, 4), Price.of(12, ResourceType.IRON),
            "免疫爆炸。"),
    END_STONE("末地石", new ItemStack(Material.ENDER_STONE, 12), Price.of(24, ResourceType.IRON),
            "用于保卫床的坚固方块。"),
    LADDER("梯子", new ItemStack(Material.LADDER, 16), Price.of(4, ResourceType.IRON),
            "可用于救助卡在树上的猫。"),
    WOOD("木板", new ItemStack(Material.WOOD, 16), Price.of(4, ResourceType.GOLD),
            "用于保卫床的优质方块。能有效抵挡稿子的破坏。"),
    OBSIDIAN("黑曜石", new ItemStack(Material.OBSIDIAN, 4), Price.of(4, ResourceType.EMERALD),
            "百分百保护你的床。");

    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ItemStack blocks;

    BlockItem(String displayName, ItemStack blocks, Price price) {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
    }

    @Override
    public String insideName() {
        return this.name();
    }

    BlockItem(String displayName, ItemStack blocks, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    BlockItem(String displayName, ItemStack blocks, Price price, String introduce)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.add(introduce);
    }

    @Override
    public boolean unlocked(GamePlayer player) {
        return false;
    }

    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(blocks.getType(), blocks.getAmount())
                .setDisplayName((enough ? "§a" : "§c") + displayName);
        List<String> lore = new ArrayList<>();
        lore.add(price.costDisplay());
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
        lore.add(enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn);
        item.setLore(lore);

        return item.build();
    }

    @Override
    public ItemStack getItem(GamePlayer player) {
        ItemBuilder item = new ItemBuilder(blocks);
        switch (blocks.getType())
        {
            case WOOL:
            case STAINED_CLAY:
            case STAINED_GLASS:
                item.setDyeColor(player.getTeam().color.dyeColor);
        }

        return item.build();
    }

    @Override
    public Price price(GamePlayer player) {
        return price;
    }






}
