package me.faintcloudy.bedwars.game.dream;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.faintcloudy.bedwars.game.GamePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface DreamManager {

    void init();
    String[] startMessage();
    default DreamShop shop()
    {
        return new DreamShop("无", new ItemStack(Material.AIR), 8) {
            @Override
            public List<ClickableItem> shopItems(GamePlayer player, InventoryContents contents) {
                return new ArrayList<>();
            }

        };
    }
}
