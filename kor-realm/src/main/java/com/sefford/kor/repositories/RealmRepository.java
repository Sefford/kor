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
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * RealmRepository is a implementation of the {@link com.sefford.kor.repositories.interfaces.Repository Repository}
 * for Realm Database.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class RealmRepository<K, V extends RealmObject & RepoElement<K> & Updateable<V>>
        implements Repository<K, V>, FastRepository<K, V> {

    protected final Realm realm;
    protected final Class<V> clazz;

    public RealmRepository(Realm realm, Class<V> clazz) {
        this.realm = realm;
        this.clazz = clazz;
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

    @Override
    public void clear() {
        realm.clear(clazz);
    }

    @Override
    public boolean contains(K id) {
        return get(id) != null;
    }

    @Override
    public void delete(K id, V element) {
        List<V> elements = new ArrayList<V>();
        elements.add(element);
        deleteAll(elements);
    }

    @Override
    public void deleteAll(List<V> elements) {
        List<K> ids = new ArrayList<K>();
        for (V element : elements) {
            ids.add(element.getId());
        }
        realm.beginTransaction();
        prepareQuery(ids).findAll().clear();
        realm.commitTransaction();
    }

    @Override
    public V get(K id) {
        List<K> ids = new ArrayList<K>();
        ids.add(id);
        return prepareQuery(ids).findFirst();
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        return prepareQuery(ids).findAll();
    }

    @Override
    public Collection<V> getAll() {
        return realm.allObjects(clazz);
    }

    RealmQuery<V> prepareQuery(Collection<K> ids) {
        RealmQuery<V> query = realm.where(clazz);
        Iterator<K> iterator = ids.iterator();
        while (iterator.hasNext()) {
            query.equalTo("id", String.valueOf(iterator.next()));
            if (iterator.hasNext()) {
                query.or();
            }
        }
        return query;
    }

    @Override
    public V save(V element) {
        realm.beginTransaction();
        V existingElement = prepareElementForSaving(element);
        realm.commitTransaction();
        return existingElement;
    }

    @Override
    public Collection<V> saveAll(Collection<V> elements) {
        realm.beginTransaction();
        List<V> results = new ArrayList<V>();
        for (V element : elements) {
            results.add(prepareElementForSaving(element));
        }
        realm.commitTransaction();
        return results;
    }

    V prepareElementForSaving(V element) {
        V existingElement = get(element.getId());
        if (existingElement == null) {
            existingElement = realm.createObject(clazz);
        }
        existingElement.update(element);
        return existingElement;
    }

    @Override
    public boolean isAvailable() {
        return realm != null;
    }
}
