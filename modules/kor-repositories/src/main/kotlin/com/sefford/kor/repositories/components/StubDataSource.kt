/*
 * Copyright (C) 2018 Saúl Díaz
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
package com.sefford.kor.repositories.components

/**
 * Helper {@link Datasource Datasource} that helps with implementation of new Datasources
 * by having by default providing an implementation of batch backed by individual
 * CRUD methods.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface StubDataSource<K, V : RepoElement<K>> : Repository<K, V> {

    override fun delete(elements: Collection<V>) {
        delete(elements.iterator())
    }

    override fun delete(vararg elements: V) {
        delete(elements.iterator())
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element -> delete(element.id, element) }
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

}
