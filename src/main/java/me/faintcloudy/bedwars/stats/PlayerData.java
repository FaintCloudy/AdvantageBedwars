package me.faintcloudy.bedwars.stats;

import com.avaje.ebean.validation.NotNull;
import fr.minuskube.inv.content.SlotPos;
import me.faintcloudy.bedwars.Bedwars;
import me.faintcloudy.bedwars.database.DataBase;
import me.faintcloudy.bedwars.database.KeyValue;
import me.faintcloudy.bedwars.database.SQLManager;
import me.faintcloudy.bedwars.game.GamePlayer;
import me.faintcloudy.bedwars.game.shop.shopitem.ShopItem;
import org.apache.commons.lang.Validate;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static me.faintcloudy.bedwars.database.SQLManager.FIELD.*;

public class PlayerData {
    public GamePlayer player;
    public CurrentGameData currentGameData;
    private HashMap<SlotPos, ShopItem> quickBuySettings;
    public String UUID;
    SQLManager sqlManager;
    DataBase database;
    public PlayerData(GamePlayer player)
    {
        this.player = player;
        this.UUID = player.player.getName();
        this.currentGameData = new CurrentGameData();
        sqlManager = Bedwars.getInstance().sqlManager;
        database = sqlManager.database;
        if (!database.isValueExists(PLAYER_STATS_TABLE, PLAYER_STATS_KV, new KeyValue(SQLManager.FIELD.UUID, this.UUID)))
        {
            database.dbInsert(PLAYER_STATS_TABLE, new KeyValue(SQLManager.FIELD.UUID, this.UUID)
                    .add(SQLManager.FIELD.LIFETIME_KILLS, 0)
                    .add(SQLManager.FIELD.DAILY_KILLS, 0).add(SQLManager.FIELD.LIFETIME_FINAL_KILLS, 0)
                    .add(SQLManager.FIELD.DAILY_FINAL_KILLS,  0).add(SQLManager.FIELD.FINAL_DEATHS, 0)
                    .add(SQLManager.FIELD.DEATHS, 0).add(SQLManager.FIELD.WINS, 0)
                    .add(SQLManager.FIELD.LOSES, 0).add(SQLManager.FIELD.WIN_STREAK, 0).add(SQLManager.FIELD.BED_BROKEN, 0));
            database.dbInsert(PLAYER_INFOS_TABLE, new KeyValue(SQLManager.FIELD.UUID, this.UUID)
                    .add(SQLManager.FIELD.QUICK_BUY, NONE).add(SQLManager.FIELD.REJOIN, "true")
                    .add(SQLManager.FIELD.COINS, 0).add(SQLManager.FIELD.EXP, 0));
            database.dbInsert(PLAYER_REWARDS_TABLE, new KeyValue(SQLManager.FIELD.UUID, this.UUID)
                    .add(SQLManager.FIELD.AUTO_ACCEPT_REQUEST, "false")
                    .add(REQUESTS, NONE).add(SQLManager.FIELD.CHALLENGES, NONE));
        }

        this.quickBuySettings = stringToQuickBuy(this.getOrDefault(QUICK_BUY, quickBuyToString(GamePlayer.defaultFastBuySettings)));
    }

    public void saveData()
    {
        currentGameData.addIntoData();
    }

    public class CurrentGameData
    {
        public int coins = 0;
        public int exp = 0;
        public int finalKills = 0;
        public int kills = 0;
        public int bedBroken = 0;
        public int win = 0;
        public int lose = 0;
        public int deaths = 0;
        public int finalDeaths = 0;

        public boolean added = false;

        public void addIntoData()
        {
            if (added)
                return;
            added = true;
            add(COINS, coins);
            add(EXP, exp);
            add(LIFETIME_FINAL_KILLS, finalKills);
            add(DAILY_FINAL_KILLS, finalKills);
            add(LIFETIME_KILLS, kills);
            add(DAILY_KILLS, kills);
            add(BED_BROKEN, bedBroken);
            add(WINS, win);
            add(LOSES, lose);
            add(FINAL_DEATHS, finalDeaths);
            add(DEATHS, deaths);
            if (lose > 0)
            {
                set(WIN_STREAK, "0");
            }
            else
            {
                add(WIN_STREAK, win);
            }

        }
    }

    public void add(String key, int valueAdd)
    {
        try
        {
            int value = Integer.parseInt(this.get(key));
            value += valueAdd;
            this.set(key, String.valueOf(value));
        }
        catch (NumberFormatException exception)
        {
            this.set(key, String.valueOf(valueAdd));
        } catch (Exception exception)
        {

            exception.printStackTrace();
        }

    }

    public void set(String key, String value)
    {
        new BukkitRunnable()
        {
            public void run()
            {
                database.dbUpdate(sqlManager.getTableName(key), new KeyValue(key, value), new KeyValue(SQLManager.FIELD.UUID, UUID));
            }
        }.runTask(Bedwars.getInstance());

    }

    public int getLevel()
    {
        return 1;
    }

    public String get(String key)
    {
        String tableName = sqlManager.getTableName(key);
        return database.dbSelectFirst(tableName, key, new KeyValue(SQLManager.FIELD.UUID, UUID));
    }

    @NotNull
    public String getOrDefault(String key, String defaultValue)
    {
        if (this.get(key) == null || this.get(key).equals(NONE))
        {
            return defaultValue;
        }
        return this.get(key);
    }

    public static String quickBuyToString(HashMap<SlotPos, ShopItem> settings)
    {
        StringBuilder builder = new StringBuilder();
        for (SlotPos slotPos : settings.keySet())
        {
            builder.append(slotPos.row).append("-").append(slotPos.column).append("-").append(settings.get(slotPos).insideName()).append(";");
        }

        return builder.toString();
    }

    public static HashMap<SlotPos, ShopItem> stringToQuickBuy(String string)
    {
        Validate.notNull(string);
        HashMap<SlotPos, ShopItem> quickBuySettings = new HashMap<>();
        String[] entries = string.split(";");
        Validate.notEmpty(entries, "error in converting string to quick buy:" + string);
        if (entries.length < 1)
        {
            Validate.notEmpty(entries, "error in converting string to quick buy:" + string);
            return GamePlayer.defaultFastBuySettings;
        }
        for (String entry : entries)
        {
            String[] split = entry.split("-");
            if (split.length < 3)
            {
                Validate.notEmpty(entries, "error in converting string to quick buy:" + string);
                return GamePlayer.defaultFastBuySettings;
            }
            int row = Integer.parseInt(split[0]);
            int column = Integer.parseInt(split[1]);
            ShopItem shopItem = ShopItem.nameToShopItemsHashMap().get(split[2]);
            quickBuySettings.put(SlotPos.of(row, column), shopItem);
        }

        return quickBuySettings;
    }

    public HashMap<SlotPos, ShopItem> getQuickBuySettings()
    {
        return quickBuySettings;
    }

    public void setQuickBuySettings(HashMap<SlotPos, ShopItem> quickBuySettings)
    {
        this.quickBuySettings = quickBuySettings;
        this.set(QUICK_BUY, quickBuyToString(quickBuySettings));
    }






}
