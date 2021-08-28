package mc.faintcloudy.bedwarslobby.database;

import mc.faintcloudy.bedwarslobby.BedwarsLobby;

import java.sql.Connection;
import java.util.Set;

import static mc.faintcloudy.bedwarslobby.database.SQLManager.FIELD.*;

public class SQLManager {
    private final Set<String> statsFields = PLAYER_STATS_KV.keyValues.keySet();
    private final Set<String> infoFields = PLAYER_INFOS_KV.keyValues.keySet();
    private final Set<String> rewardsFields = PLAYER_REWARDS_KV.keyValues.keySet();
    BedwarsLobby plugin;
    public DataBase database;
    public Connection connection;
    public SQLManager(BedwarsLobby plugin)
    {
        this.plugin = plugin;
    }

    public String getTableName(String field)
    {
        if (statsFields.contains(field))
            return PLAYER_STATS_TABLE;
        if (infoFields.contains(field))
            return PLAYER_INFOS_TABLE;
        if (rewardsFields.contains(field))
            return PLAYER_REWARDS_TABLE;
        return NONE;
    }

    public boolean isNone(String field)
    {
        return field.equals(NONE);
    }

    public interface FIELD
    {
        String NONE = "none";
        String PLAYER_STATS_TABLE = "bw_player_stats";
        String PLAYER_INFOS_TABLE = "bw_player_infos";
        String PLAYER_REWARDS_TABLE = "bw_player_rewards";
        String UUID = "uuid";

        String LIFETIME_KILLS = "lifetime_kills";
        String DAILY_KILLS = "daily_kills";
        String LIFETIME_FINAL_KILLS = "lifetime_final_kills";
        String DAILY_FINAL_KILLS = "daily_final_kills";
        String FINAL_DEATHS = "final_deaths";
        String DEATHS = "deaths";
        String EXP = "exp";
        String WINS = "wins";
        String LOSES = "loses";
        String WIN_STREAK = "win_streak";
        String COINS = "coins";
        String QUICK_BUY = "quick_buy";
        String REJOIN = "rejoin";
        String BED_BROKEN = "bed_broken";

        String AUTO_ACCEPT_REQUEST = "auto_accept_request";
        String REQUESTS = "requests";
        String CHALLENGES = "challenges";

        KeyValue PLAYER_STATS_KV = new KeyValue(FIELD.UUID, "LONGTEXT").add(FIELD.LIFETIME_KILLS, "INT")
                .add(FIELD.DAILY_KILLS, "INT").add(FIELD.LIFETIME_FINAL_KILLS, "INT")
                .add(FIELD.DAILY_FINAL_KILLS,  "INT").add(FIELD.FINAL_DEATHS, "INT")
                .add(FIELD.DEATHS, "INT").add(FIELD.WINS, "INT")
                .add(FIELD.LOSES, "INT").add(FIELD.WIN_STREAK, "INT").add(FIELD.BED_BROKEN, "INT");
        KeyValue PLAYER_INFOS_KV = new KeyValue(FIELD.UUID, "LONGTEXT").add(FIELD.QUICK_BUY, "LONGTEXT").add(FIELD.REJOIN, "LONGTEXT")
                .add(FIELD.COINS, "INT").add(FIELD.EXP, "INT");
        KeyValue PLAYER_REWARDS_KV = new KeyValue(FIELD.UUID, "LONGTEXT").add(FIELD.AUTO_ACCEPT_REQUEST, "LONGTEXT")
                .add(REQUESTS, "LONGTEXT").add(FIELD.CHALLENGES, "LONGTEXT");
    }


    public void load()
    {
        database = DataBase.create(BedwarsLobby.getInstance().getConfig());
        connection = database.getDataBaseCore().getConnection();
        if (!database.isTableExists(PLAYER_STATS_TABLE))
            database.createTables(PLAYER_STATS_TABLE, PLAYER_STATS_KV, null);
        if (!database.isTableExists(PLAYER_INFOS_TABLE))
            database.createTables(PLAYER_INFOS_TABLE, PLAYER_INFOS_KV, null);
        if (!database.isTableExists(PLAYER_REWARDS_TABLE))
            database.createTables(PLAYER_REWARDS_TABLE, PLAYER_REWARDS_KV, null);
    }

}
