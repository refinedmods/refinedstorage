package com.raoulvdberge.refinedstorage.util;

import java.util.*;

public class MultiMap<K, V> {
    private Map<K, List<V>> map = new HashMap<>();
    private List<V> allValues = new ArrayList<>();

    public List<V> get(K key) {
        List<V> values = map.get(key);

        if (values == null) {
            return Collections.emptyList();
        }

        return values;
    }

    public void put(K key, V value) {
        List<V> values = map.computeIfAbsent(key, k -> new ArrayList<>());

        allValues.add(value);

        values.add(value);
    }

    public void remove(K key, V value) {
        List<V> values = map.get(key);

        if (values != null) {
            values.remove(value);

            if (values.isEmpty()) {
                map.remove(key);
            }

            allValues.remove(value);
        }
    }

    public void clear() {
        map.clear();

        allValues.clear();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public List<V> values() {
        return allValues;
    }
}
