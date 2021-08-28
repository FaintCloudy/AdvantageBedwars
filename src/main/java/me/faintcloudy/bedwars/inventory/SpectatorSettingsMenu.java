package me.faintcloudy.bedwars.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.SpectatorSettings;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Arrays;

public class SpectatorSettingsMenu {

    public static SmartInventory menu()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(4, 9);
        builder.title("§8旁观者设置");
        builder.provider((player, contents) -> {
            SpectatorSettings settings = GamePlayer.get(player).spectatorSettings;
            contents.set(1, 1, ClickableItem.of(new ItemBuilder(Material.LEATHER_BOOTS).setDisplayName("§a关闭速度效果").build(), event -> {
                settings.speed(0);
                contents.inventory().close(player);
            }));
            contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.CHAINMAIL_BOOTS).setDisplayName("§a速度 I").build(), event -> {
                settings.speed(1);
                contents.inventory().close(player);
            }));
            contents.set(1, 3, ClickableItem.of(new ItemBuilder(Material.IRON_BOOTS).setDisplayName("§a速度 II").build(), event -> {
                settings.speed(2);
                contents.inventory().close(player);
            }));
            contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.GOLD_BOOTS).setDisplayName("§a速度 III").build(), event -> {
                settings.speed(3);
                contents.inventory().close(player);
            }));
            contents.set(1, 5, ClickableItem.of(new ItemBuilder(Material.DIAMOND_BOOTS).setDisplayName("§a速度 IV").build(), event -> {
                settings.speed(4);
                contents.inventory().close(player);
            }));
            contents.set(2, 1, ClickableItem.of(new ItemBuilder(Material.COMPASS)
                    .setDisplayName(settings.autoTeleport ? "§c停用自动传送" : "§a启用自动传送")
                    .setLore(settings.autoTeleport ? "§7点击停用自动传送！" : "§7点击启用自动传送！").build(), event -> {
                settings.autoTeleport(!settings.autoTeleport);
                contents.inventory().close(player);
            }));
            contents.set(2, 2, ClickableItem.of(settings.nightVision ? new ItemBuilder(Material.ENDER_PEARL)
                    .setDisplayName("§c禁用夜视").setLore("§7点击禁用夜视！").build() : new ItemBuilder(Material.EYE_OF_ENDER)
                    .setDisplayName("§a启用夜视").setLore("§7点击启用夜视！").build(),  event -> {
                settings.nightVision(!settings.nightVision);
                contents.inventory().close(player);
            }));
            contents.set(2, 3, ClickableItem.of(new ItemBuilder(Material.WATCH)
                    .setDisplayName(settings.autoFirstPerson ? "§c停用第一人称旁观" : "§a启用第一人称旁观")
                    .setLore(settings.autoFirstPerson ? Arrays
                            .asList("§7点击确认使用指南针时", "§7停用第一人称旁观！",
                                    "§7你也可以右键点击一位玩家", "§7来启用第一人称旁观") :
                            Arrays.asList("§7点击确认使用指南针时", "§7自动沿用第一人称旁观！",
                            "§7你也可以右键点击一位玩家", "§7来启用第一人称旁观")).build(), event -> {
                settings.autoFirstPerson(!settings.autoFirstPerson);
                contents.inventory().close(player);
            }));
            contents.set(2, 4, ClickableItem.of(settings.hideSpectators ? new ItemBuilder(Material.GLOWSTONE_DUST)
                    .setDisplayName("§a显示旁观者").setLore("§7点击以显示其他旁观者！").build() : new ItemBuilder(Material.REDSTONE)
                    .setDisplayName("§c隐藏旁观者").setLore("§7点击以隐藏其他旁观者！").build(), event -> {
                settings.hideSpectators(!settings.hideSpectators);
                contents.inventory().close(player);
            }));



        });
        return builder.build();
    }
}
