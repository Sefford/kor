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

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * BaseRepository implements a Chain of Responsibility pattern for the Repositories.
 * <p/>
 * In order to achieve true interchangeable cache levels, the default repositories are built upon
 * a chain of responibility pattern, so the updates can happen sequentially on the repository hierarchy.
 * <p/>
 * While the default implementation is based around this hierarchial organization of repositories,
 * both requests and repositories does not enforce this requirement, and unrelated - standalone
 * repos can be created through {@link com.sefford.kor.repositories.interfaces.Repository Repository Interface}
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class BaseRepository<K, V extends RepoElement<K>> implements Repository<K, V> {

    /**
     * Next level of the repository if any.
     */
    protected final Repository<K, V> currentLevel;
    /**
     * Next level of the repository if any.
     */
    protected final Repository<K, V> nextLevel;

    /**
     * Creates a new instance of a BaseRepository with next level.
     *
     * This next level can be optionally initialized to null.
     *
     * @param  currentLevel Current Level of the Repository
     * @param nextLevel Next Level of the Repository
     */
    protected BaseRepository(Repository<K, V> currentLevel, Repository<K, V> nextLevel) {
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
    }

    /**
     * Checks if the current repository has next level.
     *
     * @return TRUE if affirmative, FALSE otherwise
     */
    protected boolean hasNextLevel() {
        return nextLevel != null && nextLevel.isAvailable();
    }

    @Override
    public V save(V element) {
        final V result = currentLevel.save(element);
        if (hasNextLevel()) {
            nextLevel.save(element);
        }
        return result;
    }

    @Override
    public Collection<V> saveAll(Collection<V> elements) {
        final List<V> results = new ArrayList<V>();
        for (final V element : elements) {
            results.add(save(element));
        }
        if (hasNextLevel()) {
            nextLevel.saveAll(elements);
        }
        return results;
    }

    @Override
    public boolean contains(K id) {
        return currentLevel.contains(id) || (hasNextLevel() ? nextLevel.contains(id) : false);
    }

    @Override
    public void delete(K id, V element) {
        currentLevel.delete(id, element);
        if (hasNextLevel()) {
            nextLevel.delete(id, element);
        }
    }

    @Override
    public void deleteAll(List<V> elements) {
        currentLevel.deleteAll(elements);
        if (hasNextLevel()){
            nextLevel.deleteAll(elements);
        }
    }

    @Override
    public V get(K id) {
        V result = !currentLevel.contains(id) && hasNextLevel() ? nextLevel.get(id) : currentLevel.get(id);
        if (result != null && currentLevel.get(id) == null) {
            currentLevel.save(result);
        }
        return result;
    }

    @Override
    public void clear() {
        currentLevel.clear();
        if (hasNextLevel()) {
            nextLevel.clear();
        }
    }

    @Override
    public Collection<V> getAll() {
        final HashMap<K, V> results = new HashMap<K, V>();
        for (final V element : currentLevel.getAll()) {
            results.put(element.getId(), element);
        }
        if (hasNextLevel()) {
            for (final V nextElement : nextLevel.getAll()){
                if (!results.containsKey(nextElement.getId())) {
                    results.put(nextElement.getId(), nextElement);
                }
            }
        }
        return results.values();
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        final List<V> results = new ArrayList<V>();
        for (final K id : ids) {
            final V element = get(id);
            if (element != null) {
                results.add(element);
            }
        }
        return results;
    }

    @Override
    public boolean isAvailable() {
        return currentLevel != null && currentLevel.isAvailable();
    }
}
