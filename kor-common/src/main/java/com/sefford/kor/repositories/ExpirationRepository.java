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

import com.sefford.kor.repositories.interfaces.ExpirationPolicy;
import com.sefford.kor.repositories.interfaces.FastRepository;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Repository which allows the capabilities to implement a {@link ExpirationPolicy ExpirationPolicy} to
 * evict automatically the elements which expire.
 *
 * @author Saul Diaz Gonzalez <sefford@gmail.com>
 */
public class ExpirationRepository<K, V extends RepoElement<K>>
        implements Repository<K, V>, FastRepository<K, V> {

    /**
     * Core {@link Repository Repository} which backs up the Expiration repository itself
     */
    final Repository<K, V> repository;
    /**
     * Elements expiration policy
     */
    final ExpirationPolicy<K> policy;

    /**
     * Wraps a Repository and enables to apply an Expiration Policy to elements
     *
     * @param repository Repository to wrap.
     * @param policy     Expiration policy to apply to the elements
     */
    public ExpirationRepository(Repository<K, V> repository, ExpirationPolicy<K> policy) {
        this.repository = repository;
        this.policy = policy;
    }

    @Override
    public void clear() {
        this.policy.clear();
        this.repository.clear();
    }

    @Override
    public boolean contains(K id) {
        if (policy.isExpired(id) || !repository.contains(id)) {
            delete(id, null);
            return false;
        }
        return repository.contains(id);
    }

    @Override
    public void delete(K id, V element) {
        policy.notifyDeleted(id);
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
        if (policy.isExpired(id) || !repository.contains(id)) {
            delete(id, null);
            return null;
        }
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
    public Collection<V> getAll() {
        final Collection<V> all = repository.getAll();
        final Iterator<V> itr = all.iterator();
        while (itr.hasNext()) {
            final V element = itr.next();
            if (policy.isExpired(element.getId()) || !repository.contains(element.getId())) {
                itr.remove();
                delete(element.getId(), element);
            }
        }
        return all;
    }

    @Override
    public V save(V element) {
        policy.notifyCreated(element.getId());
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
    public boolean isAvailable() {
        return policy != null && repository != null;
    }

    @Override
    public boolean containsInMemory(K id) {
        return contains(id);
    }

    @Override
    public V getFromMemory(K id) {
        return get(id);
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
}
