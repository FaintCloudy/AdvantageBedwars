package me.faintcloudy.bedwars.game.dream.megawalls.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.megawalls.MegaWallsModeManager;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class MegaWallsClass implements Listener {

    public ArmorSet armorSet;
    public ChatColor color;
    public String name;
    public Material icon;
    public MegaWallsClass(Material icon, String name, ChatColor color, ArmorSet armorSet)
    {

        this.armorSet = armorSet;
        this.color = color;
        this.name = name;
        this.icon = icon;
    }

    public interface ClassManager
    {
        MegaWallsClass HIM = new Herobrine();
        MegaWallsClass ENDER_MAN = new EnderMan();
        MegaWallsClass ZOMBIE = new Zombie();
        MegaWallsClass SKELETON = new Skeleton();
        List<MegaWallsClass> MEGA_WALLS_CLASS_LIST = Arrays.asList(HIM, ENDER_MAN, ZOMBIE, SKELETON);
    }



    public List<Material> releaseMaterials()
    {
        return Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD);
    }

    protected String getSkillReadyChar(GamePlayer gamePlayer)
    {
        return MegaWallsModeManager.getInstance().getEnergy(gamePlayer) >= 100 ? ChatColor.GREEN + "" + ChatColor.BOLD + "✔" : ChatColor.RED + "" + ChatColor.BOLD + "✘";
    }

    public static void registerClasses()
    {
        for (MegaWallsClass megaWallsClass : ClassManager.MEGA_WALLS_CLASS_LIST)
            Bukkit.getPluginManager().registerEvents(megaWallsClass, Bedwars.getInstance());
    }

    public ItemStack getBow()
    {
        return new ItemBuilder(Material.BOW).setDisplayName(color + name + " 弓").setUnbreakable(true).build();
    }

    public abstract boolean onSkill(GamePlayer gamePlayer);
    public abstract String getSkillName();
    public abstract String getActionBarText(GamePlayer gamePlayer);
    public abstract int everyHitEnergy();

}
