package me.faintcloudy.bedwars.game.dream.ultimates;

import fr.minuskube.inv.ClickableItem;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.BedwarsMode;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.ultimates.classes.UltimateClass;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum UltimatesShopItem {
    KANGAROO(UltimateClass.KANGAROO, new ItemStack(Material.RABBIT_FOOT), "二段跳!", "在死亡时有概率保留资源!", "破坏床能够获得牛奶!"),
    SWORDSMAN(UltimateClass.SWORDSMAN, new ItemStack(Material.GOLD_SWORD), "将剑长按右键向前冲刺!", "再次长按右键回到原点!", "击杀重置冷却时间!"),
    PHYSICIAN(UltimateClass.PHYSICIAN, new ItemStack(Material.GOLDEN_APPLE), "用剑长按右键治疗自身!", "丢下药水治疗友方玩家!"),
    FROST_MAGE(UltimateClass.FROST_MAGE, new ItemStack(Material.SNOW_BALL), "丢下药水减缓敌方的速度!", "击杀能够获得雪球! (上限16个)"),
    BUILDER(UltimateClass.BUILDER, new ItemStack(Material.BRICK), "建桥!", "建墙!", "自动保护你的床", "Ÿ", "左键更改模式, 放置来触发!Ÿ", "Ÿ", "§o嘿, 尝试用它来右键你的床!Ÿ"),
    DESTROYER(UltimateClass.DESTROYER, new ItemStack(Material.FLINT_AND_STEEL), "烧毁一定范围内的羊毛!", "死亡时掉落TNT!", "破坏床时获得一个苦力怕蛋!"),
    COLLECTOR(UltimateClass.COLLECTOR, new ItemStack(Material.EMERALD), "有机会从资源生成点获得双倍钻石/绿宝石!", "随身末影箱!", "我方床被摧毁时, 获得一项免费团队升级!");

    public UltimateClass ultimateClass;
    public List<String> desc = new ArrayList<>();
    public ItemStack icon;
    public int colddown;
    UltimatesShopItem(UltimateClass ultimateClass, ItemStack icon, String... desc)
    {
        this.ultimateClass = ultimateClass;
        this.desc.addAll(Arrays.asList(desc));
        this.icon = icon;
        this.colddown = ultimateClass.colddown();
    }
    public ClickableItem toClickableItem(GamePlayer player)
    {
        ItemBuilder item = new ItemBuilder(icon.clone()).setDisplayName("§a" + ultimateClass.displayName());
        List<String> lore = new ArrayList<>();
        for (String line : desc)
        {
            if (!line.endsWith("Ÿ"))
                lore.add("§7 · " + line);
            else
                lore.add("§7" + line.replaceAll("Ÿ", ""));
        }
        if (colddown != -1)
        {
            lore.add("");
            lore.add("§7冷却: §a" + colddown + "秒");
        }
        lore.add("");
        lore.add("§e点击选择!");
        item.setLore(lore);
        item.addFlag(ItemFlag.HIDE_ENCHANTS);
        item.addFlag(ItemFlag.HIDE_ATTRIBUTES);
        return ClickableItem.of(item.build(), e ->
        {
            if (Bedwars.getInstance().game.mode != BedwarsMode.ULTIMATES)
                return;
            if (!((UltimatesModeManager) Bedwars.getInstance().game.mode.manager).ultimatesEnabled)
            {
                player.player.sendMessage("§c你不可以在此时选择超能力");
                player.player.playSound(player.player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                player.player.closeInventory();
                return;
            }

            UltimatesModeManager manager = (UltimatesModeManager) Bedwars.getInstance().game.mode.manager;
            manager.setUltimatesClass(player, ultimateClass);
            player.player.sendMessage("§a更改你的超能力为 §6" + ultimateClass.displayName() + "§a!");
            player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            player.player.closeInventory();
        });

    }
}
