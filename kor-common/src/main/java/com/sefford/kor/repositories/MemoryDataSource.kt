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
package com.sefford.kor.repositories

import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import com.sefford.kor.repositories.interfaces.Updateable

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
class MemoryDataSource<K, V>
/**
 * Creates a new instance of Memory repository
 *
 * @param cache Storage map of the repository
 */
(val cache: MutableMap<K, V>) : Repository<K, V> where V : RepoElement<K>, V : Updateable<V> {

    constructor() : this(mutableMapOf())

    override fun save(element: V): V {
        var result = get(element.id)
        if (result == null) {
            cache[element.id] = element
            result = element
        } else {
            result.update(element)
        }
        return result
    }

    override fun saveAll(elements: Collection<V>): Collection<V> {
        for (element in elements) {
            save(element)
        }
        return elements
    }

    override fun contains(id: K): Boolean {
        return cache.containsKey(id)
    }

    override fun delete(id: K, element: V?) {
        delete(id)
    }

    override fun delete(id: K) {
        cache.remove(id)
    }

    override fun deleteAll(elements: Collection<V>) {
        for (element in elements) {
            delete(element.id, element)
        }
    }

    override fun get(id: K): V? {
        return cache[id]
    }

    override fun getAll(ids: Collection<K>): Collection<V> {
        val result = ArrayList<V>()
        for (id in ids) {
            val element = get(id)
            if (element != null) {
                result.add(element)
            }
        }
        return result
    }

    override fun clear() {
        cache.clear()
    }

    override val all: Collection<V>
        get() = cache.values

    override val isAvailable: Boolean
        get() = true
}