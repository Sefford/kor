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
package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.FastRepository;
import com.sefford.kor.repositories.interfaces.Populator;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.utils.LruCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Repository which adds the capability to hold a limited number of items in cache.
 * <p>
 * The elements do use a {@link LruCache LruCache} as implementation and currently only
 * supports a quantity-based LRU (not a size-based one)
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class LruRepository<K, V extends RepoElement<K>>
        implements Repository<K, V>, FastRepository<K, V> {

    /**
     * LRU which backs up the maximum amount of elements it can hold
     */
    protected final LruCache<K> lru;
    /**
     * Core {@link Repository Repository} which backs up the LRU repository itself
     */
    protected final Repository<K, V> repository;

    /**
     * Wraps a repository which can only hold maxSize elements
     *
     * @param repository Core repository to add the capability to
     * @param maxSize    Max elements capable to add to the repository
     */
    public LruRepository(Repository<K, V> repository, int maxSize) {
        this.repository = repository;
        this.lru = new LruCache<>(maxSize);
    }

    /**
     * Wraps a repository which can only hold maxSize elements
     *
     * @param repository Core repository to add the capability to
     * @param populator Initial population strategy of the LRU
     * @param maxSize    Max elements capable to add to the repository
     */
    public LruRepository(Repository<K, V> repository, Populator populator, int maxSize) {
        this(repository, maxSize);
        populator.populate(lru);
    }

    @Override
    public V save(V element) {
        final K previous = lru.put(element.getId());
        if (previous != null) {
            repository.delete(previous, null);
        }
        return repository.save(element);
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
        return lru.contains(id);
    }

    @Override
    public void delete(K id, V element) {
        lru.remove(id);
        repository.delete(id, element);
    }

    @Override
    public void deleteAll(List<V> elements) {
        for (final V element : elements) {
            delete(element.getId(), element);
        }
    }

    @Override
    public V get(K id) {
        lru.refresh(id);
        return repository.get(id);
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        final List<V> result = new ArrayList<>();
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
        lru.clear();
        repository.clear();
    }

    @Override
    public Collection<V> getAll() {
        return repository.getAll();
    }

    @Override
    public boolean containsInMemory(K id) {
        return lru.contains(id);
    }

    @Override
    public V getFromMemory(K key) {
        return get(key);
    }

    @Override
    public Collection<V> getAllFromMemory(List<K> ids) {
        return getAll(ids);
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
        return lru != null;
    }
}
