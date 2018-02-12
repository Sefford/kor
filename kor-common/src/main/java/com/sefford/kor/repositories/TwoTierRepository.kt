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

import java.util.ArrayList
import java.util.HashMap

/**
 * TwoTierRepository implements a Chain of Responsibility pattern for the Repositories.
 *
 *
 * In order to achieve true interchangeable lru levels, the default repositories are built upon
 * a chain of responibility pattern, so the updates can happen sequentially on the repository hierarchy.
 *
 *
 * While the default implementation is based around this hierarchial organization of repositories,
 * both requests and repositories does not enforce this requirement, and unrelated - standalone
 * repos can be created through [Repository Interface][com.sefford.kor.repositories.interfaces.Repository]
 *
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
class TwoTierRepository<K, V : RepoElement<K>>
/**
 * Creates a new instance of a TwoTierRepository with next level.
 *
 *
 * This next level can be optionally initialized to null.
 *
 * @param currentLevel Current Level of the Repository
 * @param nextLevel    Next Level of the Repository
 */
(
        /**
         * Next level of the repository if any.
         */
        protected val currentLevel: Repository<K, V>,
        /**
         * Next level of the repository if any.
         */
        protected val nextLevel: Repository<K, V>) : Repository<K, V> {

    override val all: Collection<V>
        get() {
            val results = HashMap<K, V>()
            for (element in currentLevel.all) {
                results[element.id] = element
            }
            if (hasNextLevel()) {
                for (nextElement in nextLevel.all) {
                    if (!results.containsKey(nextElement.id)) {
                        results[nextElement.id] = nextElement
                    }
                }
            }
            return results.values
        }

    override val isAvailable: Boolean
        get() = currentLevel.isAvailable

    /**
     * Checks if the current repository has next level.
     *
     * @return TRUE if affirmative, FALSE otherwise
     */
    protected fun hasNextLevel(): Boolean {
        return nextLevel.isAvailable
    }

    override fun save(element: V): V {
        val result = currentLevel.save(element)
        if (hasNextLevel()) {
            nextLevel.save(element)
        }
        return result
    }

    override fun saveAll(elements: Collection<V>): Collection<V> {
        val results = ArrayList<V>()
        for (element in elements) {
            results.add(save(element))
        }
        if (hasNextLevel()) {
            nextLevel.saveAll(elements)
        }
        return results
    }

    override fun contains(id: K): Boolean {
        return currentLevel.contains(id) or (hasNextLevel() && nextLevel.contains(id))
    }

    override fun delete(id: K, element: V?) {
        currentLevel.delete(id, element)
        if (hasNextLevel()) {
            nextLevel.delete(id, element)
        }
    }

    override fun delete(id: K) {
        currentLevel.delete(id)
        if (hasNextLevel()) {
            nextLevel.delete(id)
        }
    }

    fun deleteAll(elements: List<V>) {
        currentLevel.deleteAll(elements)
        if (hasNextLevel()) {
            nextLevel.deleteAll(elements)
        }
    }

    override fun get(id: K): V? {
        var element: V? = if (currentLevel.contains(id)) currentLevel[id] else if (hasNextLevel()) nextLevel[id] else null
        if (element == null && hasNextLevel() && currentLevel.contains(id) && nextLevel.contains(id)) {
            element = nextLevel[id]
        }
        if (element != null && !currentLevel.contains(id)) {
            currentLevel.save(element)
        }
        return element
    }

    override fun clear() {
        currentLevel.clear()
        if (hasNextLevel()) {
            nextLevel.clear()
        }
    }

    override fun getAll(ids: Collection<K>): Collection<V> {
        val results = ArrayList<V>()
        for (id in ids) {
            val element = get(id)
            if (element != null) {
                results.add(element)
            }
        }
        return results
    }

    override fun deleteAll(elements: Collection<V>) {
        for (element in elements) {
            delete(element.id, element)
        }
    }
}
