package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Dreadlord extends MegaWallsClass {
    public Dreadlord() {
        super(Material.NETHER_BRICK_ITEM, "恐惧魔王", ChatColor.DARK_RED, new ArmorSet() {
            @Override
            public ItemStack getWeapon() {
                return new ItemStack(Material.AIR);
            }
        });
    }

    @Override
    public boolean onSkill(GamePlayer gamePlayer) {
        return false;
    }

    @Override
    public String getSkillName() {
        return "影爆";
    }

    @Override
    public String getActionBarText(GamePlayer gamePlayer) {
        return null;
    }

    @Override
    public int everyHitEnergy() {
        return 12;
    }
}
