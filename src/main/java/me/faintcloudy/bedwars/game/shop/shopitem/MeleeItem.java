package me.faintcloudy.bedwars.game.shop.shopitem;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.ShopItemType;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum MeleeItem implements ShopItem {
    STONE_SWORD("石剑", new ItemBuilder(Material.STONE_SWORD).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).build(), Price.of(10, ResourceType.IRON)),
    IRON_SWORD("铁剑", new ItemBuilder(Material.IRON_SWORD).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).build(), Price.of(7, ResourceType.GOLD)),
    DIAMOND_SWORD("钻石剑", new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).build(), Price.of(4, ResourceType.EMERALD)),
    KNOCK_STICK("击退棒 (击退 I)", new ItemBuilder(Material.STICK).addEnchantment(Enchantment.KNOCKBACK, 1).build(), Price.of(5, ResourceType.GOLD));


    List<Price> prices = new ArrayList<>();
    List<String> introduces = new ArrayList<>();
    Price price;
    String displayName;
    ItemStack sword;

    MeleeItem(String displayName, ItemStack sword, Price price)
    {
        this.displayName = displayName;
        this.sword = sword;
        this.price = price;
    }

    MeleeItem(String displayName, ItemStack sword, Price price, String... introduces)
    {
        this.displayName = displayName;
        this.sword = sword;
        this.price = price;
        this.introduces.addAll(Arrays.asList(introduces));
    }

    MeleeItem(String displayName, ItemStack sword, Price price, String introduce)
    {
        this.displayName = displayName;
        this.sword = sword;
        this.price = price;
        this.introduces.add(introduce);
    }

    @Override
    public ItemStack showItem(GamePlayer player, ShopItemType type) {
        boolean enough = ShopItem.enoughPrice(player, price);
        ItemBuilder item = new ItemBuilder(sword.clone())
                .setDisplayName((enough ? "§a" : "§c") + displayName)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addFlag(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();
        lore.add(price.costDisplay());
        if (player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS) > 0 && sword.getType().name().contains("SWORD"))
        {
            item = item.addGlow();
            lore.add("§7已升级: §e锋利 " + ShopItem.toRome(player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS)));
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
        lore.add(enough ? "§e点击购买！" : "§c你没有足够的" + price.resource.cn);
        item.setLore(lore);

        return item.build();
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
    public ItemStack getItem(GamePlayer player) {
        ItemBuilder sword = new ItemBuilder(this.sword);
        if (this.sword.getType().name().contains("SWORD"))
        {
            if (player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS) > 0)
                sword.addEnchantment(Enchantment.DAMAGE_ALL, player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS));
        }
        return sword.build();
    }

    @Override
    public Price price(GamePlayer player) {
        return price;
    }



    public static void coverWoodSword(Player player, ItemStack sword)
    {
        PlayerInventory inv = player.getInventory();
        if (sword.getType() == Material.WOOD_SWORD)
        {
            inv.addItem(sword);
            return;
        }
        boolean covered = false;
        int pos = -1;
        for (int i = 0;i<inv.getContents().length;i++)
        {
            if (inv.getContents()[i] == null)
                continue;
            ItemStack item = inv.getContents()[i];
            if (item.getType() == Material.WOOD_SWORD)
            {
                if (!covered)
                {
                    pos = i;
                    covered = true;
                }
                item.setType(Material.AIR);
            }
        }
        if (pos != -1)
        {
            inv.setItem(pos, sword);
        }
        else
        {
            inv.addItem(sword);
        }
    }


}
