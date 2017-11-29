/*
 * Copyright (C) 2014 Saúl Díaz
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
package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.FastRepository;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.interfaces.Updateable;

import java.util.*;

/**
 * Simple abstraction of a repository that only supports volatile memory caching.
 * <p/>
 * The fastest and simplest way to implement a memory repository is through a Map. This implementation
 * of a memory repository can be kind of tailored through the use of different map subclasses as
 * HashMap (a typical implementation) or TreeMap for sorted access.
 * <p/>
 * A limitation of the MemoryDataSource is that it does not constraint itself to a memory size. The
 * developer is intended to manually clear unused entries.
 * <p/>
 * Declaring additional memory repositories is as easy as extending this repository with particular
 * classes for Keys and Values.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class MemoryDataSource<K, V extends RepoElement<K> & Updateable<V>>
        implements Repository<K, V>, FastRepository<K, V> {

    /**
     * Map to store the Key, Value references on the repository
     */
    protected final Map<K, V> cache;

    /**
     * Creates a new instance of Memory repository with a default Hashmap implementation
     */
    public MemoryDataSource() {
        this(new HashMap<K, V>());
    }

    /**
     * Creates a new instance of Memory repository
     *
     * @param cache Storage map of the repository
     */
    public MemoryDataSource(Map<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public V save(V element) {
        V result = get(element.getId());
        if (result == null) {
            cache.put(element.getId(), element);
            result = element;
        } else {
            result.update(element);
        }
        return result;
    }

    @Override
    public Collection<V> saveAll(Collection<V> elements) {
        for (final V element : elements) {
            save(element);
        }
        return elements;
    }

    @Override
    public boolean contains(K id) {
        return cache.containsKey(id);
    }

    @Override
    public void delete(K id, V element) {
        cache.remove(id);
    }

    @Override
    public void deleteAll(List<V> elements) {
        for (final V element : elements) {
            delete(element.getId(), element);
        }
    }

    @Override
    public V get(K id) {
        return cache.get(id);
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        List<V> result = new ArrayList<V>();
        for (final K id : ids) {
            final V element = get(id);
            if (element != null) {
                result.add(element);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<V> getAll() {
        return cache.values();
    }

    @Override
    public boolean containsInMemory(K id) {
        return contains(id);
    }

    @Override
    public V getFromMemory(K key) {
        return get(key);
    }

    @Override
    public Collection<V> getAllFromMemory(List<K> ids) {
        return getAll();
    }

    @Override
    public V saveInMemory(V element) {
        return save(element);
    }

    @Override
    public Collection<V> saveAllInMemory(Collection<V> elements) {
        return saveAll(elements);
    }

    @Override
    public boolean isAvailable() {
        return cache != null;
    }
}