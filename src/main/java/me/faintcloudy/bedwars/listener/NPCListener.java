package me.faintcloudy.bedwars.listener;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.GameState;
import me.faintcloudy.bedwars.game.shop.ItemShop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class NPCListener implements Listener {
    public static List<NPC> teamShops = new ArrayList<>();
    public static List<NPC> itemShops = new ArrayList<>();

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event)
    {
        if (Bedwars.getInstance().game.state == GameState.GAMING && GamePlayer.get(event.getClicker()).state == GamePlayer.PlayerState.ALIVE)
        {
            if (event.getNPC().getEntity().hasMetadata("Shop-Type-Item"))
            {
                ItemShop.FAST_BUY.open(event.getClicker());
            }
            else if (event.getNPC().getEntity().hasMetadata("Shop-Type-Team"))
            {
                Bedwars.getInstance().TEAM_SHOP.open(event.getClicker());
            }
        }

    }
}
