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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Simple abstraction of a repository that only supports volatile memory caching.
 * <p/>
 * The fastest and simplest way to implement a memory repository is through a Map. This implementation
 * of a memory repository can be kind of tailored through the use of different map subclasses as
 * HashMap (a typical implementation) or TreeMap for sorted access.
 * <p/>
 * A limitation of the MemoryRepository is that it does not constraint itself to a memory size. The
 * developer is intended to manually clear unused entries or implements its own with LRU Cache.
 * <p/>
 * Declaring additional memory repositories is as easy as extending this repository with particular
 * classes for Keys and Values.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class MemoryRepository<K, V extends RepoElement<K> & Updateable<V>>
        extends BaseRepository<K, V> implements FastRepository<K, V> {

    /**
     * Map to store the Key, Value references on the repository
     */
    protected final Map<K, V> cache;

    /**
     * Creates a new instance of Memory repository
     *
     * @param nextLevel Repository to provide the next level of caching
     * @param cache     Storage map of the repository
     */
    protected MemoryRepository(Repository<K, V> nextLevel, Map<K, V> cache) {
        super(nextLevel);
        this.cache = cache;
    }

    @Override
    public V save(V element) {
        final V result = get(element.getId());
        if (result == null) {
            cache.put(element.getId(), element);
        } else {
            result.update(element);
        }
        if (hasNextLevel()) {
            nextLevel.save(element);
        }
        return result == null ? element : result;
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
        return cache.containsKey(id) || (hasNextLevel() ? nextLevel.contains(id) : false);
    }

    @Override
    public void delete(K id, V element) {
        cache.remove(id);
        if (hasNextLevel()) {
            nextLevel.delete(id, element);
        }
    }

    @Override
    public void deleteAll(List<V> elements) {
        for (final V element : elements) {
            delete(element.getId(), element);
        }
    }

    @Override
    public V get(K id) {
        V result = cache.get(id) == null && hasNextLevel() ? nextLevel.get(id) : cache.get(id);
        if (result != null && cache.get(id) == null) {
            cache.put(id, result);
        }
        return result;
    }

    @Override
    public void clear() {
        cache.clear();
        if (hasNextLevel()) {
            nextLevel.clear();
        }
    }

    @Override
    public Collection<V> getAll() {
        return cache.values();
    }

    @Override
    public boolean containsInMemory(K id) {
        return cache.containsKey(id);
    }

    @Override
    public V getFromMemory(K key) {
        return cache.get(key);
    }

    @Override
    public Collection<V> getAllFromMemory(List<K> ids) {
        List<V> results = new ArrayList<V>();
        for (K id : ids) {
            V element = getFromMemory(id);
            if (element != null) {
                results.add(element);
            }
        }
        return results;
    }

    @Override
    public V saveInMemory(V element) {
        V result;
        if (containsInMemory(element.getId())) {
            result = getFromMemory(element.getId());
            result.update(element);
        } else {
            cache.put(element.getId(), element);
            result = element;
        }
        return result;
    }

    @Override
    public Collection<V> saveAllInMemory(Collection<V> elements) {
        List<V> results = new ArrayList<V>();
        for (V element : elements) {
            results.add(saveInMemory(element));
        }
        return results;
    }

    @Override
    public boolean isAvailable() {
        return cache != null;
    }
}