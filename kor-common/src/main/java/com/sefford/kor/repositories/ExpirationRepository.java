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
 * Created by sefford on 6/2/17.
 */
public class ExpirationRepository<K, V extends RepoElement<K>>
        implements Repository<K, V>, FastRepository<K, V> {

    final Repository<K, V> repository;
    final ExpirationPolicy<K> policy;

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
        if (policy.isExpired(id)) {
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
        if (policy.isExpired(id)) {
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
            if (policy.isExpired(element.getId())) {
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
