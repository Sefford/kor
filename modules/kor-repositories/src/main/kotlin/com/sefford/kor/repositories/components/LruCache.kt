/*
 * Copyright (C) 2017 Saúl Díaz
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
package com.sefford.kor.repositories.components

import kotlin.collections.LinkedHashSet

/**
 * Basic implementation of a LRU by quantity rather than weight.
 *
 *
 * Take into account this LRU works on quantity (e.g. 200 elements) rather
 * than a size of the element itself, so measure carefully the average size
 * of the element because you might find your cache occupying too much
 * memory or disk space.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class LruCache<K>
/**
 * Creates a new LRU cache of max size maxSize
 *
 * @param maxSize Maximum amount of element this LRU will hold at most
 */
(
        /**
         * Max size the cache will not grow over
         */
        internal val maxSize: Int) {

    /**
     * Backbone of the LRU
     */
    internal val values: LinkedHashSet<K>

    init {
        this.values = LinkedHashSet(0, DEFAULT_LOAD_FACTOR)
    }

    fun put(value: K?): K? {
        if (value == null) {
            return null
        }
        var previous: K? = null
        if (values.contains(value)) {
            refresh(value)
        } else if (values.size == maxSize) {
            val iterator = values.iterator()
            previous = iterator.next()
            iterator.remove()
        }
        values.add(value)
        return previous
    }

    fun refresh(value: K) {
        values.remove(value)
        values.add(value)
    }

    fun remove(value: K) {
        values.remove(value)
    }

    operator fun contains(key: K): Boolean {
        return values.contains(key)
    }

    fun clear() {
        values.clear()
    }

    companion object {
        const val DEFAULT_LOAD_FACTOR = .75f
    }
}
