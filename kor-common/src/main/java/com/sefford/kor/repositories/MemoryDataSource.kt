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

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.sefford.kor.interactors.RepositoryError
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
(private val cache: MutableMap<K, V>) : Repository<K, V> where V : RepoElement<K> {

    constructor() : this(mutableMapOf())

    override fun save(element: V): Either<RepositoryError, V> {
        val result = get(element.id)
        when (result) {
            is Either.Right -> {
                if (result.b is Updateable<*>) {
                    (result.b as Updateable<V>).update(element)
                } else {
                    cache[element.id] = element
                }
                return Right(result.b)
            }
            is Either.Left -> {
                cache[element.id] = element
                return Right(element)
            }
        }
    }

    override fun save(elements: Collection<V>): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(vararg elements: V): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(elements: Iterator<V>): Collection<V> {
        val results = mutableListOf<V>()
        elements.forEach { element -> save(element).map { results.add(it) } }
        return results
    }

    override fun contains(id: K): Boolean {
        return cache.containsKey(id)
    }

    override fun delete(id: K, element: V) {
        delete(id)
    }

    override fun delete(id: K) {
        cache.remove(id)
    }

    override fun delete(elements: Collection<V>) {
        delete(elements.iterator())
    }

    override fun delete(vararg elements: V) {
        delete(elements.iterator())
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element -> delete(element.id, element) }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (!cache.contains(id)) {
            return Left(RepositoryError.NotFound(id))
        }
        return Right(cache[id]!!)
    }

    override fun get(ids: Collection<K>): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(vararg ids: K): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val result = ArrayList<V>()
        ids.forEach { id -> get(id).map { result.add(it) } }
        return result
    }

    override fun clear() {
        cache.clear()
    }

    override val all: Collection<V>
        get() = cache.values

    override val isReady: Boolean
        get() = true
}