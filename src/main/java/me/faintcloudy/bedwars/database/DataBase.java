package me.faintcloudy.bedwars.database;

import me.faintcloudy.bedwars.Bedwars;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataBase
{
    private final DataBaseCore dataBaseCore;
    
    public DataBase(DataBaseCore core)
    {
        dataBaseCore = core;
    }

    public static DataBase create(ConfigurationSection dbConfig)
    {
        Type type = Type.valueOf(dbConfig.getString("type", "SQLITE").toUpperCase());
        switch (type)
        {
            case MYSQL:
                return new DataBase(new MySQLCore(dbConfig));
            default:
                return new DataBase(new SQLiteCore(Bedwars.getInstance(), dbConfig));
        }
    }
    
    public boolean close()
    {
        try
        {
            dataBaseCore.getConnection().close();
            return true;
        }
        catch (SQLException e)
        {
        }
        return false;
    }
    
    public boolean copyTo(DataBaseCore db)
    {
        try
        {
            String src = dataBaseCore.getConnection().getMetaData().getURL();
            String des = db.getConnection().getMetaData().getURL();
            ResultSet rs = dataBaseCore.getConnection().getMetaData().getTables(null,
                    null, "%", null);
            List<String> tables = new LinkedList<String>();
            while (rs.next())
            {
                tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            int s = 0;
            for (String table : tables)
            {
                System.out.println("开始复制源数据库中的表 " + table + " ...");
                if (!table.toLowerCase().startsWith("sqlite_autoindex_"))
                {
                    System.out.println("清空目标数据库中的表 " + table + " ...");
                    db.execute("DELETE FROM " + table);
                    rs = dataBaseCore.executeQuery("SELECT * FROM " + table);
                    int n = 0;
                    String query = "INSERT INTO " + table + " VALUES (";
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                    {
                        query = query + "?, ";
                    }
                    query = query.substring(0, query.length() - 2) + ")";
                    PreparedStatement ps = db.getConnection().prepareStatement(query);
                    long time = System.currentTimeMillis();
                    while (rs.next())
                    {
                        n++;
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                        {
                            ps.setObject(i, rs.getObject(i));
                        }
                        ps.addBatch();
                        if (n % 100 == 0)
                        {
                            ps.executeBatch();
                        }
                        if (System.currentTimeMillis() - time > 500L)
                        {
                            System.out.println("已复制 " + n + " 条记录...");
                            time = System.currentTimeMillis();
                        }
                    }
                    System.out.println("数据表 " + table + " 复制完成 共 " + n + " 条记录...");
                    s += n;
                    ps.executeBatch();
                    rs.close();
                }
            }
            System.out.println("成功从 " + src + " 复制 " + s + " 条数据到 " + des + " ...");
            db.getConnection().close();
            dataBaseCore.getConnection().close();
            return true;
        }
        catch (SQLException e)
        {
        }
        return false;
    }
    
    public boolean createTables(String tableName, KeyValue fields, String Conditions)
    {
        try
        {
            dataBaseCore.createTables(tableName, fields, Conditions);
            return isTableExists(tableName);
        }
        catch (Exception e)
        {
            sqlerr("创建数据表 " + tableName + " 异常(内部方法)...", e);
        }
        return false;
    }
    
    public int dbDelete(String tableName, KeyValue fields)
    {
        String sql = "DELETE FROM `" + tableName + "` WHERE " + fields.toWhereString();
        try
        {
            return dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return 0;
    }
    
    public boolean dbExist(String tableName, KeyValue fields)
    {
        String sql = "SELECT * FROM " + tableName + " WHERE " + fields.toWhereString();
        try
        {
            return dataBaseCore.executeQuery(sql).next();
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return false;
    }
    
    public int dbInsert(String tabName, KeyValue fields)
    {
        String sql = "INSERT INTO `" + tabName + "` " + fields.toInsertString();
        try
        {
            return dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return 0;
    }
    
    public List<KeyValue> dbSelect(String tableName, KeyValue fields,
            KeyValue selCondition)
    {
        String sql = "SELECT " + fields.toKeys() + " FROM `" + tableName + "`"
                + (selCondition == null ? ""
                        : new StringBuilder().append(" WHERE ")
                                .append(selCondition.toWhereString()).toString());
        List<KeyValue> kvlist = new ArrayList<KeyValue>();
        try
        {
            ResultSet dbresult = dataBaseCore.executeQuery(sql);
            while (dbresult.next())
            {
                KeyValue kv = new KeyValue();
                for (String col : fields.getKeys())
                {
                    kv.add(col, dbresult.getString(col));
                }
                kvlist.add(kv);
            }
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return kvlist;
    }
    
    public String dbSelectFirst(String tableName, String fields, KeyValue selConditions)
    {
        String sql = "SELECT " + fields + " FROM " + tableName + " WHERE "
                + selConditions.toWhereString() + " LIMIT 1";
        try
        {
            ResultSet dbresult = dataBaseCore.executeQuery(sql);
            if (dbresult.next())
            {
                return dbresult.getString(fields);
            }
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return null;
    }
    
    public int dbUpdate(String tabName, KeyValue fields, KeyValue upCondition)
    {
        String sql = "UPDATE `" + tabName + "` SET " + fields.toUpdateString() + " WHERE "
                + upCondition.toWhereString();
        try
        {
            return dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return 0;
    }
    
    public DataBaseCore getDataBaseCore()
    {
        return dataBaseCore;
    }
    
    public boolean isValueExists(String tableName, KeyValue fields, KeyValue selCondition)
    {
        String sql = "SELECT " + fields.toKeys() + " FROM `" + tableName + "`"
                + (selCondition == null ? ""
                        : new StringBuilder().append(" WHERE ")
                                .append(selCondition.toWhereString()).toString());
        try
        {
            ResultSet dbresult = dataBaseCore.executeQuery(sql);
            return dbresult.next();
        }
        catch (Exception e)
        {
            sqlerr(sql, e);
        }
        return false;
    }
    
    public boolean isFieldExists(String tableName, KeyValue fields)
    {
        try
        {
            DatabaseMetaData dbm = dataBaseCore.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next())
            {
                ResultSet f = dbm.getColumns(null, null, tableName, fields.getKeys()[0]);
                return f.next();
            }
        }
        catch (SQLException e)
        {
            sqlerr("判断 表名:" + tableName + " 字段名:" + fields.getKeys()[0] + " 是否存在时出错!", e);
        }
        return false;
    }
    
    public boolean isTableExists(String tableName)
    {
        try
        {
            DatabaseMetaData dbm = dataBaseCore.getConnection().getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        }
        catch (SQLException e)
        {
            sqlerr("判断 表名:" + tableName + " 是否存在时出错!", e);
        }
        return false;
    }
    
    public void sqlerr(String sql, Exception e)
    {
        System.out.println("数据库操作出错: " + e.getMessage());
        System.out.println("SQL查询语句: " + sql);
    }

    enum Type
    {
        MYSQL, SQLITE
    }
}
