package com.sefford.kor.repositories;

import com.google.gson.Gson;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

import java.util.*;

/**
 * Created by sefford on 6/5/17.
 */

public class MemoryJsonDataSource<K, V extends RepoElement<K>> implements Repository<K, V> {

    final Gson gson;
    final Class<V> clazz;
    final Map<K, String> cache;

    public MemoryJsonDataSource(Gson gson, Class<V> clazz) {
        this.gson = gson;
        this.clazz = clazz;
        this.cache = new HashMap<>();
    }

    @Override
    public void clear() {
        cache.clear();
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
        for (V element : elements) {
            delete(element.getId(), element);
        }
    }

    @Override
    public V get(K id) {
        return gson.fromJson(cache.get(id), clazz);
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        final List<V> elements = new ArrayList<>();
        for (K id : ids) {
            if (cache.containsKey(id)) {
                elements.add(get(id));
            }
        }
        return elements;
    }

    @Override
    public Collection<V> getAll() {
        final List<V> elements = new ArrayList<>();
        for (K id : cache.keySet()) {
            elements.add(get(id));
        }
        return elements;
    }

    @Override
    public V save(V element) {
        cache.put(element.getId(), gson.toJson(element));
        return element;
    }

    @Override
    public Collection<V> saveAll(Collection<V> elements) {
        for (V element : elements) {
            save(element);
        }
        return elements;
    }

    @Override
    public boolean isAvailable() {
        return cache != null;
    }
}
