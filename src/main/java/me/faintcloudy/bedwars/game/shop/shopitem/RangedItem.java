package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RangedItem implements ShopItem {
    ARROW("箭", new ItemStack(Material.ARROW, 8), Price.of(2, ResourceType.GOLD)),
    BOW("弓", new ItemBuilder(Material.BOW).setUnbreakable(true).build(), Price.of(12, ResourceType.GOLD)),
    BOW_POWER_1("弓 (力量 I)", new ItemBuilder(Material.BOW).setUnbreakable(true)
            .addEnchantment(Enchantment.ARROW_DAMAGE, 1).build(), Price.of(24, ResourceType.GOLD)),
    BOW_POWER_1_PUNCH_1("弓 (力量 I, 冲击 I)", new ItemBuilder(Material.BOW).setUnbreakable(true)
            .addEnchantment(Enchantment.ARROW_DAMAGE, 1).addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
            .build(), Price.of(6, ResourceType.EMERALD));

    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ItemStack blocks;

    RangedItem(String displayName, ItemStack blocks, Price price)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
    }
    @Override
    public String insideName() {
        return this.name();
    }

    RangedItem(String displayName, ItemStack blocks, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    RangedItem(String displayName, ItemStack blocks, Price price, String introduce)
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
