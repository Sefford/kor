package com.sefford.kor.repositories

import arrow.core.Either
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.repositories.components.Populator
import com.sefford.kor.repositories.components.RepoElement
import com.sefford.kor.repositories.components.Repository
import com.sefford.kor.repositories.components.LruCache

class LruRepository<K, V : RepoElement<K>>(private val lru: LruCache<K>, private val repository: Repository<K, V>) : Repository<K, V> by repository {

    constructor(repository: Repository<K, V>, maxSize: Int) : this(LruCache<K>(maxSize), repository)

    constructor(repository: Repository<K, V>, populator: Populator<K>, maxSize: Int) : this(repository, maxSize) {
        populator.populate(lru)
    }

    override fun save(element: V): Either<RepositoryError, V> {
        val result = repository.save(element)
        result.map { evict(it.id) }
        return result
    }

    override fun save(elements: Iterator<V>): Collection<V> {
        val results = repository.save(elements)
        results.forEach { element: V -> evict(element.id) }
        return results
    }

    override fun save(elements: Collection<V>): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(vararg elements: V): Collection<V> {
        return save(elements.iterator())
    }

    override fun contains(id: K): Boolean {
        val idInLru = lru.contains(id)
        if (idInLru && !repository.contains(id)) {
            lru.remove(id)
            return false
        }
        return idInLru
    }

    override fun delete(id: K, element: V) {
        lru.remove(id)
        repository.delete(id, element)
    }

    override fun delete(id: K) {
        lru.remove(id)
        repository.delete(id)
    }

    override fun delete(vararg elements: V) {
        elements.forEach { element: V -> lru.remove(element.id) }
        repository.delete(*elements)
    }

    override fun delete(elements: Collection<V>) {
        elements.forEach { element: V -> lru.remove(element.id) }
        repository.delete(elements)
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element: V ->
            lru.remove(element.id)
            delete(element.id)
        }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        lru.refresh(id)
        val element = repository[id]
        element.swap().map { delete(id) }
        return element
    }

    override fun get(ids: Collection<K>): Collection<V> {
        val results = repository.get(ids)
        results.forEach { element -> lru.refresh(element.id) }
        return results
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val results = repository.get(ids)
        results.forEach { element -> lru.refresh(element.id) }
        return results
    }

    override fun get(vararg ids: K): Collection<V> {
        val results = repository.get(*ids)
        results.forEach { element -> lru.refresh(element.id) }
        return results
    }

    override fun clear() {
        lru.clear()
        repository.clear()
    }

    private fun evict(id: K) {
        val previous = lru.put(id)
        if (previous != null) {
            repository.delete(previous)
        }
    }
}
