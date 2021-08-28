package mc.faintcloudy.bedwarslobby;

import mc.faintcloudy.bedwarslobby.database.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Papi extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "bw";
    }

    @Override
    public String getAuthor() {
        return "FaintCloudy";
    }

    @Override
    public String getVersion() {
        return BedwarsLobby.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return PlayerData.getStats(player.getName()).get(params);
    }
}
