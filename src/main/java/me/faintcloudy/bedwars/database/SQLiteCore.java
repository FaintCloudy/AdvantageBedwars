package me.faintcloudy.bedwars.database;

import me.faintcloudy.bedwars.Bedwars;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLiteCore extends DataBaseCore
{
    private static String driverName = "org.sqlite.JDBC";
    private static Logger logger = Bedwars.getInstance().getLogger();
    private Connection connection;
    private File dbFile;

    public SQLiteCore(File dbFile)
    {
        this.dbFile = dbFile;
        if (this.dbFile.exists())
        {
            try
            {
                this.dbFile.createNewFile();
            }
            catch (IOException e)
            {
                logger.warning("数据库文件 " + dbFile.getAbsolutePath() + " 创建失败!");
                e.printStackTrace();
            }
        }
        try
        {
            Class.forName(driverName).newInstance();
        }
        catch (Exception e)
        {
            logger.warning("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
            e.printStackTrace();
        }
    }

    public SQLiteCore(Plugin plugin, ConfigurationSection cfg)
    {
        this(plugin, cfg.getString("Database.database"));
    }

    public SQLiteCore(Plugin plugin, String filename)
    {
        dbFile = new File(plugin.getDataFolder(), filename + ".db");
        if (dbFile.exists())
        {
            try
            {
                dbFile.createNewFile();
            }
            catch (IOException e)
            {
                logger.warning("数据库文件 " + dbFile.getAbsolutePath() + " 创建失败!");
                e.printStackTrace();
            }
        }
        try
        {
            Class.forName(driverName).newInstance();
        }
        catch (Exception e)
        {
            logger.warning("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
            e.printStackTrace();
        }
    }

    public SQLiteCore(String filepath)
    {
        this(new File(filepath));
    }

    @Override
    public boolean createTables(String tableName, KeyValue fields, String Conditions)
            throws SQLException
    {
        String sql = "CREATE TABLE IF NOT EXISTS `%s` ( %s )";
        return execute(String.format(sql, tableName,
                fields.toCreateString().replace("AUTO_INCREMENT", "AUTOINCREMENT")));
    }

    public String getAUTO_INCREMENT()
    {
        return "AUTOINCREMENT";
    }

    @Override public Connection getConnection()
    {
        try
        {
            if ((connection != null) && (!connection.isClosed()))
            {
                return connection;
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            return connection;
        }
        catch (SQLException e)
        {
            logger.warning("数据库操作出错: " + e.getMessage());
            logger.warning("数据库文件: " + dbFile.getAbsolutePath());
        }
        return null;
    }
}
