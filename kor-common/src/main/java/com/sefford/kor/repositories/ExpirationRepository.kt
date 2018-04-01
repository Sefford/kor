package com.sefford.kor.repositories

import arrow.core.Either
import arrow.core.Left
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.ExpirationPolicy
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import java.util.*

class ExpirationRepository<K, V : RepoElement<K>>(private val repository: Repository<K, V>, private val policy: ExpirationPolicy<K>) : Repository<K, V> by repository {

    override fun clear() {
        this.policy.clear()
        this.repository.clear()
    }

    override fun contains(id: K): Boolean {
        if (policy.isExpired(id) or !repository.contains(id)) {
            delete(id)
            return false
        }
        return repository.contains(id)
    }

    override fun delete(id: K, element: V) {
        policy.notifyDeleted(id)
        repository.delete(id, element)
    }

    override fun delete(id: K) {
        policy.notifyDeleted(id)
        repository.delete(id)
    }

    override fun delete(vararg elements: V) {
        elements.forEach { element -> policy.notifyDeleted(element.id) }
        repository.delete(*elements)
    }

    override fun delete(elements: Collection<V>) {
        elements.forEach { element -> policy.notifyDeleted(element.id) }
        repository.delete(elements)
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element ->
            policy.notifyDeleted(element.id)
            repository.delete(element.id, element)
        }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (policy.isExpired(id) or !repository.contains(id)) {
            delete(id)
            return Left(RepositoryError.NotFound(id))
        }
        return repository[id]
    }

    override fun save(element: V): Either<RepositoryError, V> {
        val result = repository.save(element)
        if (result.isRight()) {
            policy.notifyCreated(element.id)
        }
        return result
    }

    override fun save(vararg elements: V): Collection<V> {
        val result = repository.save(*elements)
        result.forEach { element -> policy.notifyCreated(element.id) }
        return result
    }

    override fun save(elements: Collection<V>): Collection<V> {
        val result = repository.save(elements)
        result.forEach { element -> policy.notifyCreated(element.id) }
        return result
    }

    override fun save(elements: Iterator<V>): Collection<V> {
        val result = repository.save(elements)
        result.forEach { element -> policy.notifyCreated(element.id) }
        return result
    }

    override fun get(vararg ids: K): Collection<V> {
        val result = repository.get(ids.filter { id -> !policy.isExpired(id) })
        ids.filter { id -> policy.isExpired(id) }.forEach { id -> delete(id) }
        return result
    }

    override fun get(ids: Collection<K>): Collection<V> {
        val result = repository.get(ids.filter { id -> !policy.isExpired(id) })
        ids.filter { id -> policy.isExpired(id) }.forEach { id -> delete(id) }
        return result
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val result = repository.get(ids)
        result.filter { element -> policy.isExpired(element.id) }.forEach { element -> delete(element.id) }
        return result.filter { element -> !policy.isExpired(element.id) }
    }

    override val all: Collection<V>
        get() : Collection<V> {
            return repository.all.toMutableList().filter { element ->
                val expired = policy.isExpired(element.id)
                if (expired) {
                    delete(element.id, element)
                }
                !expired
            }
        }
}