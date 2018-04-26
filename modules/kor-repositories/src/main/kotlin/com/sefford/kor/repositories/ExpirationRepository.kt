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
package com.sefford.kor.repositories

import arrow.core.*
import com.sefford.kor.repositories.components.*

/**
 * Repository that allows to apply a keep-alive policy over a repository.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class ExpirationRepository<K, V : RepoElement<K>>(private val repository: Repository<K, V>,
                                                  private val policy: ExpirationPolicy<K>) : StubDataSource<K, V> {
    override fun clear() {
        this.policy.clear()
        this.repository.clear()
    }

    override fun contains(id: K): Boolean {
        if (elementIsAvailable(id)) {
            delete(id)
            return false
        }
        return repository.contains(id)
    }

    override fun delete(id: K) {
        policy.notifyDeleted(id)
        repository.delete(id)
    }

    override fun delete(id: K, element: V) {
        policy.notifyDeleted(id)
        repository.delete(id, element)
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element ->
            policy.notifyDeleted(element.id)
            repository.delete(element.id, element)
        }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (elementIsAvailable(id)) {
            delete(id)
            return Left(RepositoryError.NotFound(id))
        }
        return repository[id]
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val result = repository.get(ids).partition { element -> !policy.isExpired(element.id) }
        result.second.forEach { element -> delete(element.id) }
        return result.first
    }

    override fun save(element: V): Either<RepositoryError, V> {
        val result = repository.save(element)
        result.map { policy.notifyCreated(it.id) }
        return result
    }

    override val all: Collection<V>
        get() : Collection<V> {
            val result = repository.all.partition { element -> !policy.isExpired(element.id) }
            result.second.forEach { element -> delete(element.id) }
            return result.first
        }

    override val isReady: Boolean
        get() = repository.isReady

    private fun elementIsAvailable(id: K) = policy.isExpired(id) or !repository.contains(id)
}
