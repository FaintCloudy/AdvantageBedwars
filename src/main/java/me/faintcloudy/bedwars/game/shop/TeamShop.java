package me.faintcloudy.bedwars.game.shop;

import com.google.gson.internal.$Gson$Preconditions;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.game.shop.shopitem.UpgradeItem;
import me.faintcloudy.bedwars.game.team.upgrade.TrapUpgrade;
import me.faintcloudy.bedwars.listener.EventCaller;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

public class TeamShop {
    List<UpgradeItem> shopItems;
    SmartInventory inventory;
    public TeamShop()
    {
        shopItems = Arrays.asList(UpgradeItem.values());
        this.build();
    }

    public static TrapShop TRAP_SHOP = new TrapShop();


    public void open(Player player)
    {
        inventory.open(player);
    }

    public void build()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(6, 9);
        builder.title("升级与陷阱");
        builder.provider((player1, contents) -> {
            GamePlayer player = GamePlayer.get(player1);
            for (int i = 1;i<7;i++)
            {
                UpgradeItem shopItem = shopItems.get(i-1);
                contents.set(1, i, ClickableItem.of(shopItem.showItem(player, null), event ->
                {
                    if (shopItem.unlocked(player))
                        return;

                    Price price = shopItem.price(player);
                    boolean enough = ShopItem.enoughPrice(player, price);
                    if (!enough)
                    {
                        player.sendMessage("§c不足! 还需要" + price.resource.cn + "x" + ShopItem.need(player, price) + "!");
                        player.player.playSound(player.player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        contents.inventory().close(player.player);
                        return;
                    }

                    ItemUtils.take(player.player.getInventory(), price.resource.material, price.amount);
                    player.player.playSound(player.player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    String dp = shopItem.showItem(player, null).getItemMeta().getDisplayName();
                    StringBuilder display = new StringBuilder(dp).deleteCharAt(0).deleteCharAt(0);
                    for (GamePlayer member : player.getTeam().players)
                    {
                        member.sendMessage("§a" + player.player.getName() + " 购买了 §6" + display);
                    }


                    player.player.getInventory().addItem(shopItem.getItem(player));


                    contents.inventory().open(player1);
                }));
            }
            ClickableItem glassPane = ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setDisplayName("§8⇧ §7可购买的")
                    .setLore("§8⇩ §7陷阱队列")
                    .setDyeColor(DyeColor.GRAY).build());
            for (int i = 0;i<9;i++)
            {
                contents.set(2, i, glassPane);
            }

            contents.set(1, 7, ClickableItem.of(new ItemBuilder(Material.LEATHER)
                    .setDisplayName("§e购买一个陷阱")
                    .setLore("§7已购买的陷阱将加入右边的队列中.", "", "§e点击浏览！").build(), event ->
            {
                TRAP_SHOP.open(player1);
            }));

            for (int i = 0;i<3;i++)
            {
                boolean hasTrap = player.getTeam().trapInfos.get(i) != null;
                String color = hasTrap ? "§a" : "§c";
                ItemBuilder item;
                String toChinese = "一";
                if (i+1 == 2)
                {
                    toChinese = "二";
                }
                else if (i+1 == 3)
                {
                    toChinese = "三";
                }

                if (hasTrap)
                {
                    UpgradeItem trapItem = UpgradeItem.getUpgradeItem(player.getTeam().trapInfos.get(i).upgrade);
                    assert trapItem != null;
                    List<String> lore = new ArrayList<>();
                    for (String in : trapItem.introduces)
                        lore.add("§7" + in);
                    lore.add("");
                    lore.add("§7第" + toChinese + "个敌人进入己方基地后");
                    lore.add("§7会触发该陷阱！");
                    lore.add("");
                    lore.add("§7购买者: " + player.getTeam().trapInfos.get(i).player.player.getName());

                    item = new ItemBuilder(trapItem.icon).setDisplayName(color + "陷阱#" + (i+1) + ": " + trapItem.displayName + "！")
                            .setLore(lore).addFlag(ItemFlag.HIDE_ATTRIBUTES);
                }
                else
                {
                    Price nextPrice = Price.of(player.getTeam().activeTraps() + 1, ResourceType.DIAMOND);
                    List<String> lore = new ArrayList<>();
                    lore.add("§7第" + toChinese + "个敌人进入己方基地后");
                    lore.add("§7会触发该陷阱！");
                    lore.add("");
                    lore.add("§7购买的陷阱会在此排列，具体");
                    lore.add("§7费用取决于已排列的陷阱数量。");
                    lore.add("");
                    lore.add("§7下一个陷阱: " + nextPrice.resource.color + nextPrice.amount + " " + nextPrice.resource.cn);

                    item = new ItemBuilder(Material.STAINED_GLASS)
                            .setDyeColor(DyeColor.WHITE).setDisplayName(color + "陷阱#" + (i+1) + ": 没有陷阱！")
                            .setLore(lore).addFlag(ItemFlag.HIDE_ATTRIBUTES);
                }
                contents.set(3, 3 + i, ClickableItem.of(item.build(), event -> {}));
            }
        });

        inventory = builder.build();
    }
}
