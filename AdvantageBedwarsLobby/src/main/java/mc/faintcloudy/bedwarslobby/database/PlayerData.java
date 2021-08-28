package mc.faintcloudy.bedwarslobby.database;

import com.avaje.ebean.validation.NotNull;
import mc.faintcloudy.bedwarslobby.BedwarsLobby;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static mc.faintcloudy.bedwarslobby.database.SQLManager.FIELD.*;

public class PlayerData {
    public String UUID;
    SQLManager sqlManager;
    DataBase database;
    public PlayerData(String name)
    {
        this.UUID = name;
        sqlManager = BedwarsLobby.getInstance().sqlManager;
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
        }.runTask(BedwarsLobby.getInstance());

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

    static List<PlayerData> data = new ArrayList<>();
    public static PlayerData getStats(String name)
    {
        for (PlayerData datum : data) {
            if (datum.UUID.equalsIgnoreCase(name))
                return datum;
        }

        PlayerData data = new PlayerData(name);
        PlayerData.data.add(data);
        return data;
    }

}
