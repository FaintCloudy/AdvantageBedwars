package me.faintcloudy.bedwars.utils;

import java.util.HashMap;

public class MapBuilder<K, V> {
    private final HashMap<K, V> map = new HashMap<>();
    public MapBuilder(K k, V v)
    {
        map.put(k, v);
    }

    public MapBuilder()
    {

    }

    public MapBuilder(HashMap<K, V> map)
    {
        this.map.putAll(map);
    }

    public MapBuilder<K, V> remove(K k)
    {
        map.remove(k);
        return this;
    }

    public MapBuilder<K, V> put(K k, V v)
    {
        map.put(k, v);
        return this;
    }

    public HashMap<K, V> build()
    {
        return map;
    }
}

