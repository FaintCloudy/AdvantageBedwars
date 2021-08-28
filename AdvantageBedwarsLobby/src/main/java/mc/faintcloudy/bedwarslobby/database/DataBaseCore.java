package mc.faintcloudy.bedwarslobby.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DataBaseCore
{
    public abstract boolean createTables(String tableName, KeyValue fields,
            String conditions) throws SQLException;
    
    public boolean execute(String sql) throws SQLException
    {
        return getStatement().execute(sql);
    }
    
    public ResultSet executeQuery(String sql) throws SQLException
    {
        return getStatement().executeQuery(sql);
    }
    
    public int executeUpdate(String sql) throws SQLException
    {
        return getStatement().executeUpdate(sql);
    }
    
    public abstract Connection getConnection();
    
    private Statement getStatement() throws SQLException
    {
        return getConnection().createStatement();
    }
}
