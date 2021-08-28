package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.events.team.TeamBedBrokeEvent;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.resource.ResourceType;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import me.faintcloudy.bedwars.game.shop.shopitem.UpgradeItem;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Collector implements UltimateClass {
    @Override
    public String displayName() {
        return "收集者";
    }

    static ItemStack enderChest = new ItemBuilder(Material.ENDER_CHEST).setDisplayName(ChatColor.LIGHT_PURPLE + "随身末影箱 §7(右键点击)").build();

    @Override
    public void init(GamePlayer player) {
        player.player.getInventory().addItem(enderChest);
    }

    @Override
    public List<ItemStack> takeWith(GamePlayer player) {
        return Collections.singletonList(enderChest);
    }

    @EventHandler
    public void onBedBroke(TeamBedBrokeEvent event)
    {
        for (GamePlayer p : event.team.getAlive())
        {
            if (manager().getUltimatesClass(p) != this)
                return;
            for (TeamUpgrade upgrade : event.team.upgradeLevels.keySet())
            {
                if (event.team.upgradeLevels.get(upgrade) >= upgrade.maxLevel())
                    continue;
                upgrade.upgrade(event.team);

                String upgradeDisplayName = UpgradeItem.getUpgradeItem(upgrade).displayName + " " +
                        ShopItem.toRome(event.team.upgradeLevels.get(upgrade));
                event.team.players.forEach(member -> member.player.sendMessage("§a" + p.player.getName() + " 的收集者能力使团队获得了 §6"
                        + upgradeDisplayName));
                break;
            }
        }
    }

    @EventHandler
    public void onEnderChest(PlayerInteractEvent event)
    {
        if (event.getAction().name().contains("RIGHT") && event.getMaterial() == Material.ENDER_CHEST)
        {
            GamePlayer player = GamePlayer.get(event.getPlayer());
            if (manager().getUltimatesClass(player) != this)
                return;
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
            Inventory chestInventory = player.player.getEnderChest();
            player.player.openInventory(chestInventory);
        }
    }

    @EventHandler
    public void onPickupResource(PlayerPickupItemEvent event)
    {
        GamePlayer player = GamePlayer.get(event.getPlayer());
        if (manager().getUltimatesClass(player) != this)
            return;
        if (event.isCancelled())
            return;
        Material type = event.getItem().getItemStack().getType();
        for (ResourceType resourceType : ResourceType.values())
        {
            if (resourceType.material == type)
            {
                int random = new Random().nextInt(99);
                if (random >= 0 && random < 50)
                {
                    player.player.getInventory().addItem(event.getItem().getItemStack().clone());
                    player.player.sendMessage("§e你获得了双倍资源");
                }
                break;
            }
        }
    }

    @Override
    public int colddown() {
        return -1;
    }
}
