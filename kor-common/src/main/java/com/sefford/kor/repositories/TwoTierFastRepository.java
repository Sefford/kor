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

import java.util.Collection;
import java.util.List;

/**
 * Extension of {@link TwoTierRepository TwoTierRepository} to support fast
 * lru saving.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class TwoTierFastRepository<K, V extends RepoElement<K>> extends TwoTierRepository<K, V> implements FastRepository<K, V> {

    /**
     * Creates a new instance of a TwoTierRepository with next level.
     * <p/>
     * This next level can be optionally initialized to null.
     *
     * @param currentLevel Current Level of the Repository
     * @param nextLevel    Next Level of the Repository
     */
    protected TwoTierFastRepository(FastRepository<K, V> currentLevel, Repository<K, V> nextLevel) {
        super((Repository<K, V>) currentLevel, nextLevel);
    }

    @Override
    public boolean containsInMemory(K id) {
        return ((FastRepository<K, V>) currentLevel).containsInMemory(id);
    }

    @Override
    public V getFromMemory(K key) {
        return ((FastRepository<K, V>) currentLevel).getFromMemory(key);
    }

    @Override
    public Collection<V> getAllFromMemory(List<K> ids) {
        return ((FastRepository<K, V>) currentLevel).getAllFromMemory(ids);
    }

    @Override
    public V saveInMemory(V element) {
        return ((FastRepository<K, V>) currentLevel).saveInMemory(element);
    }

    @Override
    public Collection<V> saveAllInMemory(Collection<V> elements) {
        return ((FastRepository<K, V>) currentLevel).saveAllInMemory(elements);
    }
}
