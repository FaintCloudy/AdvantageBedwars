package me.faintcloudy.bedwars.game.equipment;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.MapBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public enum ToolEquipment {
    AXE(new MapBuilder<Integer, ItemStack>()
            .put(0, new ItemStack(Material.AIR))
            .put(1, new ItemBuilder(Material.WOOD_AXE).setUnbreakable(true)
                    .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 1)
                    .build())
            .put(2, new ItemBuilder(Material.STONE_AXE).setUnbreakable(true)
                    .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 1).build())
            .put(3, new ItemBuilder(Material.IRON_AXE).setUnbreakable(true)
                    .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 2).build())
            .put(4, new ItemBuilder(Material.DIAMOND_AXE).setUnbreakable(true)
                    .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 3).build()).build(),
            new MapBuilder<Integer, Price>()
                    .put(1, Price.of(10, ResourceType.IRON))
                    .put(2, Price.of(10, ResourceType.IRON))
                    .put(3, Price.of(3, ResourceType.GOLD))
                    .put(4, Price.of(6, ResourceType.GOLD)).build(), new MapBuilder<Integer, String>()
            .put(1, "木斧 (效率 I)")
            .put(2, "石斧 (效率 I)")
            .put(3, "铁斧 (效率 II)")
            .put(4, "钻石斧 (效率 III)").build(), 4),
    PICKAXE(new MapBuilder<Integer, ItemStack>()
            .put(0, new ItemStack(Material.AIR))
            .put(1, new ItemBuilder(Material.WOOD_PICKAXE).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 1).build())
            .put(2, new ItemBuilder(Material.IRON_PICKAXE).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 2).build())
            .put(3, new ItemBuilder(Material.GOLD_PICKAXE).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DAMAGE_ALL, 2).build())
            .put(4, new ItemBuilder(Material.DIAMOND_PICKAXE).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).addEnchantment(Enchantment.DIG_SPEED, 3).build())
            .build(), new MapBuilder<Integer, Price>()
            .put(1, Price.of(10, ResourceType.IRON))
            .put(2, Price.of(10, ResourceType.IRON))
            .put(3, Price.of(3, ResourceType.GOLD))
            .put(4, Price.of(6, ResourceType.GOLD)).build(), new MapBuilder<Integer, String>()
            .put(1, "木稿 (效率 I)")
            .put(2, "铁稿 (效率 II)")
            .put(3, "金镐 (效率 III, 锋利 II)")
            .put(4, "钻石镐 (效率 III)").build(), 4),
    SHEARS(new MapBuilder<Integer, ItemStack>()
            .put(0, new ItemStack(Material.AIR))
            .put(1, new ItemBuilder(Material.SHEARS).setUnbreakable(true)
            .addFlag(ItemFlag.HIDE_UNBREAKABLE).build()).build(), new MapBuilder<Integer, Price>()
            .put(1, Price.of(20, ResourceType.IRON)).build(), new MapBuilder<Integer, String>()
            .put(1, "永久的 剪刀").build(), 1);
    public Map<Integer, ItemStack> items;
    public Map<Integer, Price> costs;
    public Map<Integer, String> displays;
    public int max;
    ToolEquipment(Map<Integer, ItemStack> items, Map<Integer, Price> costs, Map<Integer, String> displays, int max)
    {
        this.items = items;
        this.costs = costs;
        this.displays = displays;
        this.max = max;
    }

    public void set(GamePlayer player)
    {
        ItemBuilder item = new ItemBuilder(this.items.get(player.toolLevels.get(this)).clone());
        if (this == AXE && player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS) > 0)
            item = item.addEnchantment(Enchantment.DAMAGE_ALL, player.getTeam().upgradeLevels.get(TeamUpgrade.SHARPNESS));
        PlayerInventory inv = player.player.getInventory();
        if (player.toolLevels.get(this) <= 1 || this == SHEARS)
        {
            inv.addItem(item.build());
            return;
        }

        String materialName = this.name();

        int pos = -1;
        for (int i = 0;i<inv.getContents().length;i++)
        {
            if (inv.getContents()[i] == null)
                continue;
            ItemStack is = inv.getContents()[i];
            if (is.getType().name().split("_").length < 2)
                continue;
            if (is.getType().name().split("_")[1].equals(materialName))
            {
                pos = i;
                break;
            }
        }

        if (pos != -1)
        {
            inv.setItem(pos, item.build());
        }
        else
        {
            inv.addItem(item.build());
        }
    }
}
