package me.faintcloudy.bedwars.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LockableHashMap<K, V> extends HashMap<K, V> {
    private boolean locked = false;
    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public static <K, V> LockableHashMap<K, V> of(HashMap<K, V> hm)
    {
        LockableHashMap<K, V> map = new LockableHashMap<>();
        map.putAll(hm);
        return map;

    }

    @Override
    public V put(K key, V value) {
        if (locked)
            return null;
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (locked)
            return;
        super.putAll(m);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (locked)
            return null;
        return super.putIfAbsent(key, value);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (locked)
            return null;
        return super.compute(key, remappingFunction);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (locked)
            return null;
        return super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (locked)
            return null;
        return super.computeIfPresent(key, remappingFunction);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (locked)
            return false;
        return super.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (locked)
            return false;
        return super.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        if (locked)
            return null;
        return super.replace(key, value);
    }

    @Override
    public V remove(Object key) {
        if (locked)
            return null;
        return super.remove(key);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (locked)
            return;
        super.replaceAll(function);
    }

    @Override
    public void clear() {
        if (locked)
            return;
        super.clear();
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (locked)
            return null;
        return super.merge(key, value, remappingFunction);
    }
}
