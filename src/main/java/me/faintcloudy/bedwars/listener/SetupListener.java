package me.faintcloudy.bedwars.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetupListener implements Listener {
    public static Block selected = null;
    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            return;
        if (event.getPlayer().getItemInHand().getType() == Material.WOOD_PICKAXE)
        {
            if (event.getPlayer().hasPermission("bw.admin") || event.getPlayer().isOp())
            {
                event.setCancelled(true);
                selected = event.getBlock();
                event.getPlayer().sendMessage("§a你选择了方块 §e" + selected.getLocation().toString());
            }

        }
    }
}
