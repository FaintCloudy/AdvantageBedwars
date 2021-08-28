package me.faintcloudy.bedwars.game.dream.megawalls;

import fr.minuskube.inv.ClickableItem;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.megawalls.classes.MegaWallsClass;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ClassShopItem {
    HIM(MegaWallsClass.ClassManager.HIM, "释放一道雷电将对你周围4.5", "米内的敌人造成伤害"),
    ENDER_MAN(MegaWallsClass.ClassManager.ENDER_MAN, "瞄准范围内的敌人释放技能", "将会传送到他的位置"),
    ZOMBIE(MegaWallsClass.ClassManager.ZOMBIE, "治疗周围的队友及你自", "身§a5§7血量"),
    SKELETON(MegaWallsClass.ClassManager.SKELETON, "射出一道TNT箭将对", "你的敌人造成他自", "身血量%18的伤害");

    public MegaWallsClass megaWallsClass;
    public List<String> desc = new ArrayList<>();
    public ItemStack icon;
    ClassShopItem(MegaWallsClass megaWallsClass, String... desc)
    {
        this.megaWallsClass = megaWallsClass;
        this.desc.addAll(Arrays.asList(desc));
        this.icon = new ItemStack(megaWallsClass.icon);
    }

    public ClickableItem toClickableItem(GamePlayer player)
    {
        ItemBuilder item = new ItemBuilder(icon.clone()).setDisplayName("§a" + megaWallsClass.name);
        List<String> lore = new ArrayList<>();
        lore.add("§7技能: " + megaWallsClass.color + megaWallsClass.getSkillName());
        for (String line : desc)
        {
            if (!line.endsWith("Ÿ"))
                lore.add("§7 · " + line);
            else
                lore.add("§7" + line.replaceAll("Ÿ", ""));
        }

        lore.add("");
        lore.add(MegaWallsModeManager.getInstance().getSelectedClass(player) != megaWallsClass ? "§e点击选择!" : "§a已选择");
        item.setLore(lore);
        item.addFlag(ItemFlag.HIDE_ENCHANTS);
        item.addFlag(ItemFlag.HIDE_ATTRIBUTES);
        return ClickableItem.of(item.build(), e ->
        {
            if (Bedwars.getInstance().game.mode != BedwarsMode.MEGA_WALLS)
                return;
            if (!MegaWallsModeManager.getInstance().classesEnabled)
            {
                player.player.sendMessage("§c你不可以在此时选择职业");
                player.player.playSound(player.player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                player.player.closeInventory();
                return;
            }

            MegaWallsModeManager.getInstance().setSelectedClasses(player, megaWallsClass);
            player.player.sendMessage("§a更改你的职业为 §6" + megaWallsClass.name + "§a!");
            player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.player.closeInventory();
        });

    }
}
