package mc.faintcloudy.bedwarslobby.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql核心封装
 */
public class KeyValue {
    public final Map<String, Object> keyValues = new HashMap<>();

    public KeyValue() {
    }

    public KeyValue(String key, Object value) {
        add(key, value);
    }

    /**
     * 添加表方法
     *
     * @param key   钥匙
     * @param value 值
     * @return KeyValue
     */
    public KeyValue add(String key, Object value) {
        keyValues.put(key, value);
        return this;
    }

    public String[] getKeys() {
        return keyValues.keySet().toArray(new String[0]);
    }

    /**
     * 获得结果
     *
     * @param key 钥匙
     * @return String 结果
     */
    public String getString(String key) {
        Object obj = keyValues.get(key);
        return obj == null ? "" : obj.toString();
    }

    public Object[] getValues() {
        List<Object> keys = new ArrayList<Object>();
        for (Map.Entry<String, Object> next : keyValues.entrySet()) {
            keys.add(next.getValue());
        }
        return keys.toArray(new Object[0]);
    }

    public boolean isEmpty() {
        return keyValues.isEmpty();
    }

    public String toCreateString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> next : keyValues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("` ");
            sb.append(next.getValue());
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String toInsertString() {
        String ks = "";
        String vs = "";
        for (Map.Entry<String, Object> next : keyValues.entrySet()) {
            ks = ks + "`" + next.getKey() + "`, ";
            vs = vs + "'" + next.getValue() + "', ";
        }
        return "(" + ks.substring(0, ks.length() - 2) + ") VALUES ("
                + vs.substring(0, vs.length() - 2) + ")";
    }

    public String toKeys() {
        StringBuilder sb = new StringBuilder();
        for (Object next : keyValues.keySet()) {
            sb.append("`");
            sb.append(next);
            sb.append("`, ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public String toString() {
        return keyValues.toString();
    }

    public String toUpdateString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> next : keyValues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' ,");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String toWhereString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> next : keyValues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' and ");
        }
        return sb.substring(0, sb.length() - 5);
    }
}
