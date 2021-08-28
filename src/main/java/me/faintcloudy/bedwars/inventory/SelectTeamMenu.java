package me.faintcloudy.bedwars.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.Game;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.team.Team;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectTeamMenu {

    public static SmartInventory menu()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(1, 9);
        builder.title("§7选择队伍");
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                Game game = Bedwars.getInstance().game;
                int t = 0;
                for (Team team : game.teams) {
                    int current = team.players.size();
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("§7选择一个队伍并开始");
                    lore.add("§7你的游戏吧！");
                    lore.add("");
                    if (!team.players.isEmpty())
                    {
                        lore.add("§a当前玩家: ");
                        for (GamePlayer p : team.players)
                        {
                            lore.add(" §7- " + p.getPrefixedName());
                        }
                        lore.add("");
                    }

                    lore.add(GamePlayer.get(player).getTeam() == team ? "§a已加入" : (team.isFull() ? "§c已满" : "§e点击加入！"));
                    ItemStack item = new ItemBuilder(Material.WOOL,
                            team.players.size()).setDyeColor(team.color.dyeColor)
                            .setDisplayName(team.color.chatColor + team.color.cn + " §7(§a" + current + "§7/" + game.scale.ppt + ")")
                            .setLore(lore).build();

                    contents.set(0, t, ClickableItem.of(item, event ->
                            {
                                contents.inventory().close(player);

                                if (GamePlayer.get(player).getTeam() == team)
                                {
                                    player.sendMessage("§c你已经加入了这个队伍");
                                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                    return;
                                }

                                if (team.isFull()) {
                                    player.sendMessage("§c当前队伍已满");
                                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                    return;
                                }

                                team.joinTeam(GamePlayer.get(player));
                                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                                player.sendMessage("§a你成功加入了 " + team.color.chatColor + team.color.cn);
                                for (ItemStack wool : ItemUtils.getItemsByDisplayName("§a§l选择游戏 §7(右键点击)", player.getInventory())) {
                                    if (wool.getType() != Material.WOOL)
                                        return;

                                    wool.setDurability(team.color.dyeColor.getData());
                                }
                            }
                    ));
                    t++;
                }
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.build();
    }
}
