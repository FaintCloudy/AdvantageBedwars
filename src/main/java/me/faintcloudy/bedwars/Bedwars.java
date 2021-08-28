package me.faintcloudy.bedwars;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import lib.trove.map.hash.TIntObjectHashMap;
import me.faintcloudy.bedwars.commands.DebugCommand;
import me.faintcloudy.bedwars.commands.SetupCommand;
import me.faintcloudy.bedwars.commands.ShoutCommand;
import me.faintcloudy.bedwars.database.DataBase;
import me.faintcloudy.bedwars.database.KeyValue;
import me.faintcloudy.bedwars.database.SQLManager;
import me.faintcloudy.bedwars.game.*;
import me.faintcloudy.bedwars.game.shop.TeamShop;
import me.faintcloudy.bedwars.game.team.upgrade.TeamUpgrade;
import me.faintcloudy.bedwars.holographic.HolographicManager;
import me.faintcloudy.bedwars.holographic.SimpleHologram;
import me.faintcloudy.bedwars.inventory.FastBuySettingsMenu;
import me.faintcloudy.bedwars.inventory.SelectTeamMenu;
import me.faintcloudy.bedwars.inventory.SpectatorSettingsMenu;
import me.faintcloudy.bedwars.listener.*;
import me.faintcloudy.bedwars.scoreboard.ScoreBoardManager;
import me.faintcloudy.bedwars.stats.RewardManager;
import me.faintcloudy.bedwars.utils.MapUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.CitizensNPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bedwars extends JavaPlugin {
    private static Bedwars instance = null;
    {
        instance = this;
    }

    public static DataBase database;
    public FileConfiguration mapConfig, rewardsConfig;
    public Game game;
    public ScoreBoardManager scoreBoardManager;
    public InventoryManager inventoryManager;
    public HolographicManager holographicManager;
    public RewardManager rewardManager;
    public SQLManager sqlManager;
    public Random random = new Random();
    public static Bedwars getInstance() {
        return instance;
    }

    public HolographicManager getHolographicManager() {
        return holographicManager;
    }

    public SmartInventory SELECT_GAME_MENU, FAST_BUY_SETTINGS_MENU, SPECTATOR_SETTINGS_MENU;
    public TeamShop TEAM_SHOP;
    public void loadMenus()
    {
        SELECT_GAME_MENU = SelectTeamMenu.menu();
        FAST_BUY_SETTINGS_MENU = FastBuySettingsMenu.build();
        TEAM_SHOP = new TeamShop();
        SPECTATOR_SETTINGS_MENU = SpectatorSettingsMenu.menu();
    }

    public void initGame()
    {
        game = new Game(new GameMap(this.mapConfig), GameScale.of(this.getConfig().getInt("game-teams"),
                this.getConfig().getInt("players-per-team")), BedwarsMode.of(this.getConfig().getString("game-mode")));
    }
    public void sendToLobby(Player player)
    {
        if (this.getLobbies().isEmpty())
        {
            player.sendMessage("§c很抱歉, 目前没有任何大厅正在运行");
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        List<String> lobbies = this.getLobbies();
        out.writeUTF(lobbies.get(random.nextInt(lobbies.size())));
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public List<String> getLobbies()
    {
        List<String> lobbies = this.getConfig().getStringList("lobbies");
        if (lobbies == null)
            lobbies = new ArrayList<>();
        return lobbies;
    }

    String gameWorldName;



    @Override
    public void saveConfig() {
        try {
            this.getConfig().save(new File(this.getDataFolder(), "config.yml"));
            this.mapConfig.save(new File(this.getDataFolder(), "map.yml"));
            this.reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRewardConfig()
    {
        File file = new File(this.getDataFolder(), "rewards.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.copy(() -> getResource("rewards.yml"), file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.rewardsConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void loadMapConfig()
    {
        File file = new File(this.getDataFolder(), "map.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.mapConfig = YamlConfiguration.loadConfiguration(file);
    }


    @Override
    public void onEnable() {
        loadRewardConfig();
        holographicManager = new HolographicManager();
        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
        rewardManager = new RewardManager();
        this.gameWorldName = "world";
        loadMenus();
        loadMapConfig();

        saveDefaultConfig();
        if (!this.getConfig().getBoolean("setup"))
        {
            this.initGame();
            this.registerListener(new SpecialItemListener());
            this.registerListener(new PlayerListener());
            this.registerListener(new NPCListener());
            this.registerListener(new EventCaller());

            this.registerListener(new TeamEntitiesListener());
            this.registerListener(new SpectateListener());
            this.registerListener(new RewardListener());
            new DebugCommand().register();
            new ShoutCommand().register();
            this.scoreBoardManager = new ScoreBoardManager(this, game);
            TeamUpgrade.registerUpgrades();
        }


        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.registerListener(new SetupListener());
        SetupCommand setupCommand = new SetupCommand();
        setupCommand.register();



        reloadConfig();
        new BukkitRunnable()
        {
            public void run()
            {
                sqlManager = new SQLManager(Bedwars.this);
                sqlManager.load();
            }
        }.runTask(this);

    }

    public static void callEvent(Event event)
    {
        Bukkit.getPluginManager().callEvent(event);
    }
    @Override
    public void onDisable() {
        sqlManager.database.close();
        for (Block block : PlayerListener.blocksPut)
        {
            if (!block.getChunk().isLoaded())
                block.getChunk().load(true);
            block.setType(Material.AIR);
        }
        for (Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer("§c服务器重启");

        for (SimpleHologram hologram : new ArrayList<>(Bedwars.getInstance().getHolographicManager().getHolographics()))
            hologram.remove();
        game.gameWorld.getEntities().forEach(entity ->
        {
            if (entity instanceof Painting || entity instanceof ItemFrame)
                return;
            entity.remove();
        });
        for (NPC npc : NPCListener.itemShops) {
            npc.getOwningRegistry().deregister(npc);
            npc.destroy();
            npc.getEntity().remove();
        }
        for (NPC npc : NPCListener.teamShops)
        {
            npc.getOwningRegistry().deregister(npc);
            npc.destroy();
            npc.getEntity().remove();
        }



    }
    public void registerListener(Listener l)
    {
        Bukkit.getPluginManager().registerEvents(l, this);
    }
}
