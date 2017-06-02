package com.sefford.kor.repositories.utils;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by sefford on 6/2/17.
 */

public class LruCache<K> {

    final LinkedHashSet<K> values;
    final int maxSize;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        this.values = new LinkedHashSet<>(0, 0.75f);
    }

    public K put(K value) {
        if (value == null) {
            return null;
        }
        K previous = null;
        if (values.contains(value)) {
            refresh(value);
        } else if (values.size() == maxSize) {
            final Iterator<K> iterator = values.iterator();
            previous = iterator.next();
            iterator.remove();
        }
        values.add(value);
        return previous;
    }

    public void refresh(K value) {
        values.remove(value);
        values.add(value);
    }

    public void remove(K value) {
        values.remove(value);
    }

    public boolean contains(K key) {
        return values.contains(key);
    }

    public void clear() {
        values.clear();
    }
}
