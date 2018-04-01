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
            for (id in cache.keys) {
                val element = get(id)
                when (element) {
                    is Either.Right -> elements.add(element.b)
                }
            }
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
        for (element in elements) {
            delete(element.id, element)
        }
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
        for (id in ids) {
            val element = get(id)
            when (element) {
                is Either.Right -> elements.add(element.b)
                else -> {
                }
            }
        }
        return elements
    }

    override fun save(element: V): Either<RepositoryError, V> {
        val result = converter.serialize(element)
        when (result) {
            is Either.Left -> return Left(result.a)
            is Either.Right -> cache[element.id] = result.b
        }
        return Right(element)
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
            val result = save(element)
            when (result) {
                is Either.Right -> results.add(result.b)
            }
        }
        return results
    }
}
