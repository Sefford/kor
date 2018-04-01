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
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
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
            val nextLevelResult = nextLevel.save(element)
            return if (nextLevelResult.isRight()) nextLevelResult else currentLevelResult
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
        for (element in elements) {
            val saved = save(element)
            when (saved) {
                is Either.Right -> results.add(saved.b)
            }
        }
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
        for (element in elements) {
            delete(element.id, element)
        }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (!isReady) {
            return Left(RepositoryError.NotReady)
        }
        when {
            currentLevel.contains(id) && hasNextLevel() && nextLevel.contains(id) -> {
                val result = currentLevel[id]
                when (result) {
                    is Either.Left -> {
                        currentLevel.delete(id)
                        return propagate(nextLevel[id], currentLevel)
                    }
                    is Either.Right -> return result
                }
            }
            currentLevel.contains(id) -> return currentLevel[id]
            !currentLevel.contains(id) && hasNextLevel() && nextLevel.contains(id) -> {
                val result = nextLevel[id]
                propagate(result, currentLevel)
                return result
            }
        }
        return Left(RepositoryError.NotFound(id))
    }

    internal fun propagate(element: Either<RepositoryError, V>, propagationRepo: Repository<K, V>): Either<RepositoryError, V> {
        when (element) {
            is Either.Right -> propagationRepo.save(element.b)
        }
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
        for (id in ids) {
            val element = get(id)
            when (element) {
                is Either.Right -> results.add(element.b)
            }
        }
        return results
    }
}
