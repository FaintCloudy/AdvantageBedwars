package me.faintcloudy.bedwars.game.equipment;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ArmorEquipment {
    LEATHER_ARMOR,
    CHAINMAIL_ARMOR(new ItemBuilder(Material.CHAINMAIL_LEGGINGS)
            .setUnbreakable(true).build(), new ItemBuilder(Material.CHAINMAIL_BOOTS).setUnbreakable(true).build()),
    IRON_ARMOR(new ItemBuilder(Material.IRON_LEGGINGS)
            .setUnbreakable(true).build(), new ItemBuilder(Material.IRON_BOOTS).setUnbreakable(true).build()),
    DIAMOND_ARMOR(new ItemBuilder(Material.DIAMOND_LEGGINGS)
            .setUnbreakable(true).build(), new ItemBuilder(Material.DIAMOND_BOOTS).setUnbreakable(true).build());
    ItemStack helmet = new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.WATER_WORKER, 1)
            .setUnbreakable(true).build();
    ItemStack chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE).setUnbreakable(true).build();
    ItemStack leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).setUnbreakable(true).build();
    ItemStack boots = new ItemBuilder(Material.LEATHER_BOOTS).setUnbreakable(true).build();
    ArmorEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots)
    {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    ArmorEquipment(ItemStack leggings, ItemStack boots)
    {
        this.leggings = leggings;
        this.boots = boots;
    }

    ArmorEquipment()
    {

    }

    public Material icon()
    {
        return boots.getType();
    }

    public void set(GamePlayer player)
    {
        player.armor = this;
        PlayerInventory inv = player.player.getInventory();
        ItemBuilder helmet = new ItemBuilder(this.helmet.clone());
        ItemBuilder chestplate = new ItemBuilder(this.chestplate.clone());
        ItemBuilder leggings = new ItemBuilder(this.leggings.clone());
        ItemBuilder boots = new ItemBuilder(this.boots.clone());
        if (player.getTeam().upgradeLevels.get(TeamUpgrade.PROTECTION) > 0)
        {
            int protection = player.getTeam().upgradeLevels.get(TeamUpgrade.PROTECTION);
            helmet = helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            chestplate = chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            leggings = leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            boots = boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
        }
        DyeColor color = player.getTeam().color.dyeColor;
        helmet = helmet.setDyeColor(color);
        chestplate = chestplate.setDyeColor(color);
        leggings = leggings.setDyeColor(color);
        boots = boots.setDyeColor(color);
        inv.setHelmet(helmet.build());
        inv.setChestplate(chestplate.build());
        inv.setLeggings(leggings.build());
        inv.setBoots(boots.build());

    }
}
