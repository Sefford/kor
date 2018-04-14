package com.chicisimo.data

import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository

interface StubDataSource<K, V : RepoElement<K>> : Repository<K, V> {

    override fun delete(elements: Collection<V>) {
        delete(elements.iterator())
    }

    override fun delete(vararg elements: V) {
        delete(elements.iterator())
    }

    override fun delete(elements: Iterator<V>) {
        elements.forEach { element -> delete(element.id, element) }
    }

    override fun get(ids: Collection<K>): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(vararg ids: K): Collection<V> {
        return get(ids.iterator())
    }

    override fun get(ids: Iterator<K>): Collection<V> {
        val result = ArrayList<V>()
        ids.forEach { id -> get(id).map { result.add(it) } }
        return result
    }

    override fun save(elements: Collection<V>): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(vararg elements: V): Collection<V> {
        return save(elements.iterator())
    }

    override fun save(elements: Iterator<V>): Collection<V> {
        val results = mutableListOf<V>()
        elements.forEach { element -> save(element).map { results.add(it) } }
        return results
    }

}