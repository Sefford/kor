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
import com.sefford.kor.repositories.components.*

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
(private val cache: MutableMap<K, V>) : Repository<K, V>, StubDataSource<K, V> where V : RepoElement<K> {

    constructor() : this(mutableMapOf())

    override fun save(element: V): Either<RepositoryError, V> {
        return get(element.id).fold({
            cache[element.id] = element
            Right(element)
        }, { cachedElement ->
            if (cachedElement is Updateable<*>) {
                (cachedElement as Updateable<V>).update(element)
            } else {
                cache[cachedElement.id] = cachedElement
            }
            Right(cachedElement)
        })
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

    override fun get(id: K): Either<RepositoryError, V> {
        if (!cache.contains(id)) {
            return Left(RepositoryError.NotFound(id))
        }
        return Right(cache[id]!!)
    }

    override fun clear() {
        cache.clear()
    }

    override val all: Collection<V>
        get() = cache.values

    override val isReady: Boolean
        get() = true
}
