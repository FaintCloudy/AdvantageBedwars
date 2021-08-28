package me.faintcloudy.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class ItemBuilder
{
    private ItemStack itemStack;

    public ItemBuilder(Material material)
    {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount)
    {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, byte durability)
    {
        this.itemStack = new ItemStack(material, amount, durability);
    }

    public ItemBuilder addFlag(ItemFlag flag)
    {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flag);
        itemStack.setItemMeta(meta);
        return this;
    }

    @Override public ItemBuilder clone()
    {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder setDurability(short durability)
    {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable)
    {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String name)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level)
    {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment)
    {
        itemStack.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner)
    {
        SkullMeta im = (SkullMeta) itemStack.getItemMeta();
        im.setOwner(owner);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setEnchantMeta(Map<Enchantment, Integer> enchantments)
    {
        EnchantmentStorageMeta im = (EnchantmentStorageMeta) itemStack.getItemMeta();
        for (Map.Entry<Enchantment, Integer> m : enchantments.entrySet())
        {
            im.addStoredEnchant(m.getKey(), m.getValue().intValue(), true);
        }
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level)
    {
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setInfinityDurability()
    {
        itemStack.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder setLore(String... lore)
    {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lores = new ArrayList<String>();
        for (String line : lore)
        {
            lores.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(lores);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore)
    {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lores = new ArrayList<String>();
        for (String line : lore)
        {
            lores.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(lores);
        itemStack.setItemMeta(im);
        return this;
    }

    static List<String> armors = Arrays.asList("helmet", "chestplate", "leggings", "boots");
    @SuppressWarnings("deprecation") public ItemBuilder setDyeColor(DyeColor color)
    {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta)
        {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color.getColor());
            itemStack.setItemMeta(meta);
            return this;
        }

        for (String n : armors)
        {
            if (itemStack.getType().name().toLowerCase().contains(n))
                return this;
        }

        itemStack.setDurability(color.getData());
        return this;
    }

    public ItemBuilder addGlow()
    {
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addPotion(PotionEffect potionEffect)
    {
        PotionMeta im = (PotionMeta) itemStack.getItemMeta();
        im.setMainEffect(potionEffect.getType());
        im.addCustomEffect(potionEffect, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemStack build()
    {
        return itemStack;
    }
}
