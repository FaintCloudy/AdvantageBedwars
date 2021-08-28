package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ArmorSet {

    public ItemStack getBoots() {
        return new ItemStack(Material.IRON_BOOTS);
    }

    public ItemStack getChestplate() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    public ItemStack getHelmet() {
        return new ItemStack(Material.IRON_HELMET);
    }

    public ItemStack getLeggings() {
        return new ItemStack(Material.IRON_LEGGINGS);
    }

    public abstract ItemStack getWeapon();


    public static boolean isClassItem(ItemStack stack)
    {
        if (!stack.hasItemMeta())
            return false;
        return stack.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    }

    public void setup(Player player)
    {
        List<ItemStack> needRemove = new ArrayList<>();
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null)
                continue;
            if (isClassItem(content))
                needRemove.add(content);
        }
        for (ItemStack remove : needRemove)
            player.getInventory().removeItem(remove);
        player.getInventory().setHelmet(new ItemBuilder(this.getHelmet().clone()).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS).build());
        player.getInventory().setChestplate(new ItemBuilder(this.getChestplate().clone()).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS).build());
        player.getInventory().setLeggings(new ItemBuilder(this.getLeggings().clone()).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS).build());
        player.getInventory().setBoots(new ItemBuilder(this.getBoots().clone()).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS).build());
        player.getInventory().addItem(new ItemBuilder(this.getWeapon().clone()).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS).build());
    }
}
