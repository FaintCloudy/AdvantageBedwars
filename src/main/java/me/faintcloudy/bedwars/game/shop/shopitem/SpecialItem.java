package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SpecialItem implements ShopItem {
    GOLDEN_APPLE("金苹果", new ItemStack(Material.GOLDEN_APPLE), Price.of(3, ResourceType.GOLD), "全面治愈。"),
    BED_BUG("床蚤", new ItemStack(Material.SNOW_BALL), Price.of(40, ResourceType.IRON),
            "在雪球所落之处生成蠹虫，干扰你的对手。"),
    DREAM_DEFENDER("梦境守卫者", new ItemBuilder(Material.MONSTER_EGG, 1, (byte) EntityType.IRON_GOLEM.getTypeId()).setDisplayName("§a梦境守卫者").build(),
            Price.of(120, ResourceType.IRON), "铁傀儡能守护你的基地。"),
    FIRE_BALL("火球", new ItemStack(Material.FIREBALL), Price.of(40, ResourceType.IRON),
            "右键发射！ 击飞在桥上行走的敌人。"),
    TNT("TNT", new ItemStack(Material.TNT), Price.of(8, ResourceType.GOLD), "瞬间点燃，适用于摧毁防御工事！"),
    ENDER_PEARL("末影珍珠", new ItemStack(Material.ENDER_PEARL), Price.of(4, ResourceType.EMERALD),
            "入侵敌人基地的最快方法。"),
    WATER_BUCKET("水桶", new ItemStack(Material.WATER_BUCKET), Price.of(6, ResourceType.GOLD),
            "能很好地降低来犯敌人的速度。", "也可以抵御来自TNT的伤害。"),
    BRIDGE_EGG("搭桥蛋", new ItemBuilder(Material.EGG).setDisplayName("§a搭桥蛋").build(), Price.of(2, ResourceType.EMERALD),
            "扔出蛋后，会在其飞行轨迹上生成桥。"),
    MAGIC_MILK("魔法牛奶", new ItemStack(Material.MILK_BUCKET), Price.of(4, ResourceType.GOLD),
            "使用后，30 秒内避免触发陷阱。"),
    SPONGE("海绵", new ItemStack(Material.SPONGE, 4), Price.of(6, ResourceType.GOLD),
            "用于吸收水分。"),
    /*COMPACT_POPUP_TOWER("紧凑型速建防御塔", new ItemBuilder(Material.CHEST).setDisplayName("§a紧凑型速建防御塔").build(),
            Price.of(24, ResourceType.IRON), "建造一个速建防御塔！")*/;

    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ItemStack blocks;

    SpecialItem(String displayName, ItemStack blocks, Price price)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
    }

    SpecialItem(String displayName, ItemStack blocks, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    @Override
    public String insideName() {
        return this.name();
    }

    SpecialItem(String displayName, ItemStack blocks, Price price, String introduce)
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

        return item.build();
    }

    @Override
    public Price price(GamePlayer player) {
        return price;
    }





}
