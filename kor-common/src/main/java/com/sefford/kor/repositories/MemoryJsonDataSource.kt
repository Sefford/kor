package com.sefford.kor.repositories

import arrow.core.*
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.JsonConverter
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import java.util.*

/**
 * Created by sefford on 6/5/17.
 */

class MemoryJsonDataSource<K, V : RepoElement<K>>(internal val converter: JsonConverter<V>) : Repository<K, V> {

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

    override fun delete(vararg elements: V) {
        delete(elements.iterator())
    }

    override fun delete(elements: Collection<V>) {
        delete(elements.iterator())
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { delete(it.id, it) }
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (!cache.containsKey(id)) {
            return Either.left(RepositoryError.NotFound(id))
        }
        return converter.deserialize(cache[id])
    }

    override fun get(ids: Collection<K>): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(vararg ids: K): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val elements = ArrayList<V>()
        ids.forEach { get(it).map { elements.add(it) } }
        return elements
    }

    override fun save(element: V): Either<RepositoryError, V> {
        return converter.serialize(element).fold({ Left(it) }, {
            cache[element.id] = it
            Right(element)
        })
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
}
