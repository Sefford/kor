package com.sefford.kor.repositories

import com.sefford.kor.repositories.interfaces.Populator
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import com.sefford.kor.repositories.utils.LruCache

class LruRepository<K, V : RepoElement<K>>(val lru: LruCache<K>, val repository: Repository<K, V>) : Repository<K, V> by repository {

    constructor(repository: Repository<K, V>, maxSize: Int) : this(LruCache<K>(maxSize), repository)

    constructor(repository: Repository<K, V>, populator: Populator<K>, maxSize: Int) : this(repository, maxSize) {
        populator.populate(lru)
    }

    override fun save(element: V): V {
        val previous = lru.put(element.id)
        if (previous != null) {
            repository.delete(previous, null)
        }
        return repository.save(element)
    }

    override fun contains(id: K): Boolean {
        val idInLru = lru.contains(id)
        if (idInLru && !repository.contains(id)) {
            lru.remove(id)
            return false
        }
        return idInLru
    }

    override fun delete(id: K, element: V?) {
        lru.remove(id)
        repository.delete(id, element)
    }

    override fun delete(id: K) {
        lru.remove(id)
        repository.delete(id)
    }

    override fun get(id: K): V? {
        lru.refresh(id)
        val element = repository[id]
        if (element == null || id != element.id) {
            delete(id, null)
        }
        return element
    }


    override fun clear() {
        lru.clear()
        repository.clear()
    }
}