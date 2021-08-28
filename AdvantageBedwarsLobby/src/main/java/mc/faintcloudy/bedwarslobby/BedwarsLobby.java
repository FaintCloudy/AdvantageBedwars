package mc.faintcloudy.bedwarslobby;

import mc.faintcloudy.bedwarslobby.database.SQLManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsLobby extends JavaPlugin {

    private static BedwarsLobby instance;

    public static void setInstance(BedwarsLobby instance) {
        BedwarsLobby.instance = instance;
    }

    public static BedwarsLobby getInstance() {
        return instance;
    }

    public SQLManager sqlManager;

    @Override
    public void onEnable() {
        setInstance(this);
        saveDefaultConfig();
        reloadConfig();
        sqlManager = new SQLManager(this);
        sqlManager.load();
        PlaceholderAPI.registerPlaceholderHook(this, new Papi());
    }
}
