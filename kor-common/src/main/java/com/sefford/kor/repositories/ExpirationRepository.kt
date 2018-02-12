package com.sefford.kor.repositories

import com.sefford.kor.repositories.interfaces.ExpirationPolicy
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import java.util.ArrayList

class ExpirationRepository<K, V : RepoElement<K>>(val repository: Repository<K, V>, val policy: ExpirationPolicy<K>) : Repository<K, V> by repository {

    override fun clear() {
        this.policy.clear()
        this.repository.clear()
    }

    override fun contains(id: K): Boolean {
        if (policy.isExpired(id) || !repository.contains(id)) {
            delete(id, null)
            return false
        }
        return repository.contains(id)
    }

    override fun delete(id: K, element: V?) {
        policy.notifyDeleted(id)
        repository.delete(id, element)
    }

    override fun delete(id: K) {
        policy.notifyDeleted(id)
        repository.delete(id)
    }

    override fun get(id: K): V? {
        if (policy.isExpired(id) || !repository.contains(id)) {
            delete(id, null)
            return null
        }
        return repository[id]
    }

    override fun save(element: V): V {
        policy.notifyCreated(element.id)
        return repository.save(element)
    }


    override fun getAll(ids: Collection<K>): Collection<V> {
        val result = ArrayList<V>()
        for (id in ids) {
            val element = get(id)
            if (element != null) {
                result.add(element)
            }
        }
        return result
    }

    override val all: Collection<V>
        get() : Collection<V> {
            val all = repository.all
            val itr: MutableIterator<V> = all.iterator() as MutableIterator<V>
            while (itr.hasNext()) {
                val element = itr.next()
                if (policy.isExpired(element.id) || !repository.contains(element.id)) {
                    itr.remove()
                    delete(element.id, element)
                }
            }
            return all
        }

}