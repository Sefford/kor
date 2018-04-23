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
import com.sefford.kor.repositories.components.RepoElement
import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.repositories.components.RepositoryError
import java.util.*

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
 * @author Saul Diaz <sefford@gmail.com>
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
            val results = currentLevel.all
            if (hasNextLevel()) {
                return results.union(nextLevel.all).toList()
            }
            return results
        }

    override val isReady: Boolean
        get() = currentLevel.isReady or nextLevel.isReady

    /**
     * Checks if the current repository has next level.
     *
     * @return TRUE if affirmative, FALSE otherwise
     */
    internal fun hasNextLevel(): Boolean {
        return nextLevel.isReady
    }

    override fun save(element: V): Either<RepositoryError, V> {
        if (!isReady) {
            return Left(RepositoryError.NotReady)
        }
        val currentLevelResult = currentLevel.save(element)
        if (hasNextLevel()) {
            return nextLevel.save(element).fold({ currentLevelResult }, { Right(it) })
        }
        return currentLevelResult
    }

    override fun save(elements: Collection<V>): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(vararg elements: V): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(elements: Iterator<V>): Collection<V> {
        val results = mutableListOf<V>()
        elements.forEach { save(it).map { results.add(it) } }
        return results
    }

    override fun contains(id: K): Boolean {
        return currentLevel.contains(id) or (hasNextLevel() && nextLevel.contains(id))
    }

    override fun delete(id: K, element: V) {
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

    override fun delete(elements: Collection<V>) {
        delete(elements.iterator())
    }

    override fun delete(vararg elements: V) {
        delete(elements.iterator())
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { delete(it.id, it) }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (!isReady) {
            return Left(RepositoryError.NotReady)
        }
        when {
            currentLevel.contains(id) && hasNextLevel() && nextLevel.contains(id) ->
                return currentLevel[id].fold({
                    currentLevel.delete(id)
                    propagate(nextLevel[id], currentLevel)
                }, { Right(it) })
            currentLevel.contains(id) -> return currentLevel[id]
            !currentLevel.contains(id) && hasNextLevel() && nextLevel.contains(id) -> {
                return propagate(nextLevel[id], currentLevel)
            }
        }
        return Left(RepositoryError.NotFound(id))
    }

    internal fun propagate(element: Either<RepositoryError, V>, propagationRepo: Repository<K, V>): Either<RepositoryError, V> {
        element.map { propagationRepo.save(it) }
        return element
    }

    override fun clear() {
        currentLevel.clear()
        if (hasNextLevel()) {
            nextLevel.clear()
        }
    }

    override fun get(ids: Collection<K>): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(vararg ids: K): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val results = ArrayList<V>()
        ids.forEach { get(it).map { results.add(it) } }
        return results
    }
}
