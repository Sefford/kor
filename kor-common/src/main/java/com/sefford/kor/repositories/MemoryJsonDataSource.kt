package com.sefford.kor.repositories

import com.google.gson.Gson
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository

import java.util.*

/**
 * Created by sefford on 6/5/17.
 */

class MemoryJsonDataSource<K, V : RepoElement<K>>(internal val gson: Gson,
                                                  internal val clazz: Class<V>) : Repository<K, V> {

    internal val cache: MutableMap<K, String> = mutableMapOf()

    override val all: Collection<V>
        get() {
            val elements = ArrayList<V>()
            for (id in cache.keys) {
                elements.add(get(id)!!)
            }
            return elements
        }

    override val isAvailable: Boolean
        get() = true

    override fun clear() {
        cache.clear()
    }

    override fun contains(id: K): Boolean {
        return cache.containsKey(id)
    }

    override fun delete(id: K, element: V?) {
        delete(id)
    }

    override fun delete(id: K) {
        cache.remove(id)
    }

    override fun deleteAll(elements: Collection<V>) {
        for (element in elements) {
            delete(element.id, element)
        }
    }

    override fun get(id: K): V? {
        return gson.fromJson(cache[id], clazz)
    }

    override fun getAll(ids: Collection<K>): Collection<V> {
        val elements = ArrayList<V>()
        for (id in ids) {
            if (cache.containsKey(id)) {
                elements.add(get(id)!!)
            }
        }
        return elements
    }

    override fun save(element: V): V {
        cache[element.id] = gson.toJson(element)
        return element
    }

    override fun saveAll(elements: Collection<V>): Collection<V> {
        for (element in elements) {
            save(element)
        }
        return elements
    }
}
