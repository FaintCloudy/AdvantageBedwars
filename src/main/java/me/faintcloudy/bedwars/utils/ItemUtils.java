package me.faintcloudy.bedwars.utils;

import net.minecraft.server.v1_8_R3.ItemSaddle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static boolean isSword(Material material)
    {
        return containsNameOfMaterial("sword", material);
    }

    public static boolean containsNameOfMaterial(String name, Material material)
    {
        return material.name().toLowerCase().contains(name.toLowerCase());
    }

    public static void consumeItemInHand(Player player)
    {
        ItemStack item = player.getItemInHand().clone();
        if (item.getAmount() <= 1)
            item.setType(Material.AIR);
        else
            item.setAmount(item.getAmount()-1);
        player.setItemInHand(item);
    }

    public static List<ItemStack> getItems(Inventory inventory, Material material)
    {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory.getContents())
        {
            if (item == null)
                continue;
            if (item.getType() == material)
                items.add(item);
        }

        return items;
    }

    public static void take(Inventory inventory, Material material, int amount)
    {
        for (ItemStack item : inventory.getContents())
        {
            if (item == null)
                continue;
            if (item.getType() != material)
                continue;
            if (amount <= 0)
                break;
            if (amount >= item.getAmount())
            {
                amount -= item.getAmount();
                inventory.removeItem(item);
            }
            else
            {
                item.setAmount(item.getAmount()-amount);
                amount = 0;
            }

        }
    }

    public static boolean containsItemWithMeta(ItemMeta meta, Inventory inv)
    {
        for (ItemStack item : inv.getContents())
        {
            if (item == null)
                continue;

            if (item.getItemMeta() == meta)
                return true;
        }

        return false;
    }

    public static List<ItemStack> getItemsByDisplayName(String displayName, Inventory inv)
    {
        List<ItemStack> items = new ArrayList<>();
        if (displayName == null)
            return new ArrayList<>();

        for (ItemStack item : inv.getContents())
        {
            if (item == null)
                continue;

            if (!item.hasItemMeta())
                continue;

            if (item.getItemMeta().getDisplayName() == null)
                continue;

            if (item.getItemMeta().getDisplayName().equals(displayName))
                items.add(item);
        }

        return items;

    }

    public static boolean isAxe(Material material)
    {
        return containsNameOfMaterial("axe", material);
    }

    public static boolean isPickaxe(Material material)
    {
        return containsNameOfMaterial("pickaxe", material);
    }

    public static boolean isSpade(Material material)
    {
        return containsNameOfMaterial("spade", material);
    }

    public static boolean isHoe(Material material)
    {
        return containsNameOfMaterial("hoe", material);
    }

}
