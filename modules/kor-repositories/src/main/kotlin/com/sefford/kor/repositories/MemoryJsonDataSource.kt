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
 * Data Source to persist Json directly to memory
 *
 * @author Saul Diaz <sefford@gmail.com>
 */

class MemoryJsonDataSource<K, V : RepoElement<K>>
/**
 * New instance to MemoryJsonDataSource
 *
 * @param converter Custom Json converter
 */
constructor(internal val converter: JsonConverter<V>) : StubDataSource<K, V> {

    internal val cache: MutableMap<K, String> = mutableMapOf()

    override val all: Collection<V>
        get() {
            val elements = ArrayList<V>()
            cache.keys.forEach { get(it).map { elements.add(it) } }
            return elements
        }

    override val isReady: Boolean
        get() = true

    override fun clear() {
        cache.clear()
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
        if (!cache.containsKey(id)) {
            return Either.left(RepositoryError.NotFound(id))
        }
        return converter.deserialize(cache[id])
    }

    override fun save(element: V): Either<RepositoryError, V> {
        return converter.serialize(element).fold({ Left(it) }, { json ->
            cache[element.id] = json
            Right(element)
        })
    }
}
