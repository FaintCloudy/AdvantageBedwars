package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PotionItem implements ShopItem {
    SPEED_POTION("速度 II 药水 (45 秒)", new ItemBuilder(Material.POTION)
            .addPotion(new PotionEffect(PotionEffectType.SPEED, 45 * 20, 1))
            .setDisplayName(ChatColor.ITALIC + "速度 II 药水 (45 秒)")
            .setLore(ChatColor.BLUE + "速度 II (0:45)").build(),
            Price.of(1, ResourceType.EMERALD), ChatColor.BLUE + "速度 II (0:45)"),
    JUMP_POTION("跳跃 V 药水 (45 秒)", new ItemBuilder(Material.POTION)
            .addPotion(new PotionEffect(PotionEffectType.JUMP, 45 * 20, 4))
            .setDisplayName(ChatColor.ITALIC + "跳跃 V 药水 (45 秒)")
            .setLore(ChatColor.BLUE + "跳跃 V (0:45)").build(),
            Price.of(1, ResourceType.EMERALD), ChatColor.BLUE + "跳跃 V (0:45)"),
    INVISIBILITY_POTION("隐身药水 (30 秒)", new ItemBuilder(Material.POTION)
            .addPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 0))
            .setDisplayName(ChatColor.ITALIC + "隐身药水 (30 秒)")
            .setLore(ChatColor.BLUE + "完全隐身 (0:30)").build(),
            Price.of(2, ResourceType.EMERALD), ChatColor.BLUE + "完全隐身 (0:30)");

    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ItemStack blocks;

    PotionItem(String displayName, ItemStack blocks, Price price)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
    }

    PotionItem(String displayName, ItemStack blocks, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    PotionItem(String displayName, ItemStack blocks, Price price, String introduce)
    {
        this.displayName = displayName;
        this.blocks = blocks;
        this.price = price;
        this.introduces.add(introduce);
    }
    @Override
    public String insideName() {
        return this.name();
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
