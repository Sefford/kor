/*
 * Copyright (C) 2017 Saúl Díaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sefford.kor.repositories.utils;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Basic implementation of a LRU by quantity rather than weight.
 * <p>
 * Take into account this LRU works on quantity (e.g. 200 elements) rather
 * than a size of the element itself, so measure carefully the average size
 * of the element because you might find your cache occupying too much
 * memory or disk space.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class LruCache<K> {

    /**
     * Backbone of the LRU
     */
    final LinkedHashSet<K> values;
    /**
     * Max size the cache will not grow over
     */
    final int maxSize;

    /**
     * Creates a new LRU cache of max size maxSize
     *
     * @param maxSize Maximum amount of element this LRU will hold at most
     */
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
