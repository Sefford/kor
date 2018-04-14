package com.sefford.kor.repositories

import arrow.core.*
import com.chicisimo.data.StubDataSource
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.JsonConverter
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import java.util.*

/**
 * Created by sefford on 6/5/17.
 */

class MemoryJsonDataSource<K, V : RepoElement<K>>(internal val converter: JsonConverter<V>) : Repository<K, V>, StubDataSource<K, V> {

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
        return converter.serialize(element).fold({ Left(it) }, {
            cache[element.id] = it
            Right(element)
        })
    }
}
