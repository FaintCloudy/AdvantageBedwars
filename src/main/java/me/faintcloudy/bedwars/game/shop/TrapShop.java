package me.faintcloudy.bedwars.game.shop;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.shopitem.Price;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.game.shop.shopitem.UpgradeItem;
import me.faintcloudy.bedwars.game.team.upgrade.TrapUpgrade;
import me.faintcloudy.bedwars.listener.EventCaller;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import me.faintcloudy.bedwars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.beans.beancontext.BeanContext;
import java.util.Arrays;
import java.util.List;

public class TrapShop {
    SmartInventory inventory;
    public TrapShop()
    {
        this.build();
    }
    List<UpgradeItem> shopItems = Arrays.asList(UpgradeItem.ITS_TRAP, UpgradeItem.COUNTER_ATTACK_TRAP, UpgradeItem.ALARM_TRAP, UpgradeItem.MINER_FATIGUE_TRAP);

    public void build()
    {
        SmartInventory.Builder builder = SmartInventory.builder();
        builder.size(3, 9);
        builder.title("选择陷阱");
        builder.provider((player1, contents) -> {
            GamePlayer player = GamePlayer.get(player1);
            int slot = 0;
            for (UpgradeItem shopItem : shopItems) {
                slot++;
                contents.set(1, slot, ClickableItem.of(shopItem.showItem(player, null), event ->
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

                    if (shopItem.upgrade instanceof TrapUpgrade)
                    {
                        player.getTeam().trapInfos.put(player.getTeam().trapInfos.size(), TrapInfo.newInstance(player, (TrapUpgrade) shopItem.upgrade));
                    }

                    player.player.getInventory().addItem(shopItem.getItem(player));


                    contents.inventory().open(player1);
                    EventCaller.callInventoryChangeEvent(player1);
                }));
                contents.set(2, 4, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("§a返回")
                        .setLore("§7至升级与陷阱").build(), event -> Bedwars.getInstance().TEAM_SHOP.open(player1)));
        }
        });

        inventory = builder.build();
    }

    public void open(Player player)
    {
        inventory.open(player);
    }
}
