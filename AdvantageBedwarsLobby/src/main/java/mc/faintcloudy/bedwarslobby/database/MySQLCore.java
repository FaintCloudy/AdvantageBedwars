package mc.faintcloudy.bedwarslobby.database;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLCore extends DataBaseCore
{
    private static String driverName = "com.mysql.jdbc.Driver";
    private String username;
    private String password;
    private Connection connection;
    private String url;
    
    public MySQLCore(ConfigurationSection cfg)
    {
        this(cfg.getString("Database.ip"), cfg.getInt("Database.port"), cfg.getString("Database.database"),
                cfg.getString("Database.username"), cfg.getString("Database.password"));
    }
    
    public MySQLCore(String host, int port, String dbname, String username,
            String password)
    {
        url = ("jdbc:mysql://" + host + ":" + port + "/" + dbname);
        this.username = username;
        this.password = password;
        try
        {
            Class.forName(driverName).newInstance();
        }
        catch (Exception e)
        {
            System.out.println("数据库初始化失败 请检查驱动 " + driverName + " 是否存在!");
        }
    }
    
    @Override
    public boolean createTables(String tableName, KeyValue fields, String conditions)
            throws SQLException
    {
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` ( "
                + fields.toCreateString()
                + (conditions == null ? ""
                        : new StringBuilder(" , ").append(conditions).toString())
                + " ) ENGINE = MyISAM DEFAULT CHARSET=GBK;";
        return execute(sql);
    }
    
    @Override
    public Connection getConnection()
    {
        try
        {
            if ((connection != null) && (!connection.isClosed()))
            {
                return connection;
            }
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        }
        catch (SQLException e)
        {
            System.out.println("数据库操作出错: " + e.getMessage());
            System.out.println("登录URL: " + url);
            System.out.println("登录账户: " + username);
            System.out.println("登录密码: " + password);
        }
        return null;
    }
}
