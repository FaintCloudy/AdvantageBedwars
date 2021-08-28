package me.faintcloudy.bedwars.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TargetSelectMenu {

    public static SmartInventory menu()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.title("§8传送器");

        List<GamePlayer> allAlive = Bedwars.getInstance().game.getAlive();
        int rows = (int) Math.ceil(allAlive.size() / 9D);
        if (rows == 0)
            rows = 1;
        builder.size(rows, 9);
        builder.provider((player, contents) -> {
            for (GamePlayer alive : allAlive)
            {
                double health = alive.player.getHealth() / alive.player.getMaxHealth();
                double food = alive.player.getFoodLevel() / 20D;
                ItemStack item = new ItemBuilder(Material.SKULL_ITEM)
                        .setSkullOwner(alive.player.getName()).setDisplayName(alive.getPrefixedName())
                        .setLore("§7血量: §f" + (int)(health*100) + "%", "§7饱和度: §f" + (int)(food*100) + "%", "",
                                "§7左键点击旁观！", "§7右键点击举报！")
                        .build();
                contents.add(ClickableItem.of(item, event ->
                {
                    if (event.getClick() == ClickType.LEFT)
                    {
                        GamePlayer.get(player).spectatorSettings.target(alive);
                        contents.inventory().close(player);
                    }
                    else if (event.getClick() == ClickType.RIGHT)
                    {
                        player.sendMessage("§c尚未开发");
                        contents.inventory().close(player);
                    }

                }));
            }
        });

        return builder.build();
    }
}
