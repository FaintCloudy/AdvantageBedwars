package me.faintcloudy.bedwars.game.dream.ultimates.classes;

import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.dream.ultimates.UltimatesModeManager;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface UltimateClass extends Listener {

    String displayName();
    void init(GamePlayer player);

    UltimateClass SWORDSMAN = new Swordsman();
    UltimateClass BUILDER = new Builder();
    UltimateClass COLLECTOR = new Collector();
    UltimateClass DESTROYER = new Destroyer();
    UltimateClass FROST_MAGE = new FrostMage();
    UltimateClass KANGAROO = new Kangaroo();
    UltimateClass PHYSICIAN = new Physician();

    int colddown();

    default UltimatesModeManager manager()
    {
        if (Bedwars.getInstance().game.mode.manager instanceof UltimatesModeManager)
            return (UltimatesModeManager) Bedwars.getInstance().game.mode.manager;
        return null;
    }

    default void disable(GamePlayer player)
    {

    }

    default List<ItemStack> takeWith(GamePlayer player)
    {
        return Collections.emptyList();
    }
}
