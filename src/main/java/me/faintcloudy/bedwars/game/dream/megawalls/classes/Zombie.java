package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Zombie extends MegaWallsClass {
    public Zombie() {
        super(Material.ROTTEN_FLESH, "僵尸", ChatColor.DARK_GREEN, new ArmorSet() {
            @Override
            public ItemStack getWeapon() {
                return new ItemBuilder(Material.IRON_SWORD).setDisplayName(ChatColor.DARK_GREEN + "僵尸 剑")
                        .addEnchantment(Enchantment.DURABILITY, 10).setUnbreakable(true).build();
            }

            @Override
            public ItemStack getChestplate() {
                return new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(ChatColor.DARK_GREEN + "僵尸 胸甲")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                        .addEnchantment(Enchantment.DURABILITY, 2).setUnbreakable(true).build();
            }

            @Override
            public ItemStack getHelmet() {
                return new ItemBuilder(Material.IRON_HELMET).setDisplayName(ChatColor.DARK_GREEN + "僵尸 头盔")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchantment(Enchantment.DURABILITY, 2)
                        .setUnbreakable(true).build();
            }
        });
    }

    @Override
    public boolean onSkill(GamePlayer gamePlayer) {
        gamePlayer.heal(5);
        gamePlayer.playSound(Sound.FIREWORK_BLAST, true);
        gamePlayer.playEffectAround(Effect.HEART, 6);
        gamePlayer.sendMessage(color + "你的治愈之环技能为你治疗了 5 血量");
        return true;
    }

    @Override
    public String getSkillName() {
        return "治愈之环";
    }

    @Override
    public String getActionBarText(GamePlayer gamePlayer) {
        return color + "" + ChatColor.BOLD +  "治愈之环 " + this.getSkillReadyChar(gamePlayer);
    }

    @Override
    public int everyHitEnergy() {
        return 12;
    }
}
