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
package com.sefford.kor.repositories.utils

import org.junit.Before
import org.junit.Test

import org.hamcrest.core.Is.`is`
import org.junit.Assert.*

/**
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
class LruCacheTest {

    lateinit var lruCache: LruCache<Int>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        lruCache = LruCache(3)
    }

    @Test
    @Throws(Exception::class)
    fun testPutCorrectlyWhenNotInLru() {
        val previous = lruCache.put(0)

        assertNull(previous)
        assertTrue(lruCache.contains(0))
    }

    @Test
    @Throws(Exception::class)
    fun testPutSameValueIsCorrectlyRefreshed() {
        lruCache.put(0)
        lruCache.put(1)
        lruCache.put(2)
        val previous = lruCache.put(0)

        assertNull(previous)
        val itr = lruCache.values.iterator()
        assertThat(itr.next(), `is`<Any>(1))
        assertThat(itr.next(), `is`<Any>(2))
        assertThat(itr.next(), `is`<Any>(0))
    }

    @Test
    @Throws(Exception::class)
    fun testPutNull() {
        val previous = lruCache.put(null)

        assertNull(previous)
        assertTrue(lruCache.values.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testOrderIsRespected() {
        lruCache.put(0)
        lruCache.put(1)
        lruCache.put(2)

        val itr = lruCache.values.iterator()
        assertThat(itr.next(), `is`<Any>(0))
        assertThat(itr.next(), `is`<Any>(1))
        assertThat(itr.next(), `is`<Any>(2))
    }

    @Test
    @Throws(Exception::class)
    fun testItemsAreProperlyEvicted() {
        lruCache.put(0)
        lruCache.put(1)
        lruCache.put(2)
        val previous = lruCache.put(3)

        assertThat<Int>(previous, `is`(0))
        val itr = lruCache.values.iterator()
        assertThat(itr.next(), `is`<Any>(1))
        assertThat(itr.next(), `is`<Any>(2))
        assertThat(itr.next(), `is`<Any>(3))
    }

    @Test
    @Throws(Exception::class)
    fun testRefreshBringsElementToFront() {
        lruCache.put(0)
        lruCache.put(1)
        lruCache.put(2)

        lruCache.refresh(0)

        assertThat(lruCache.values.iterator().next(), `is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun testIsRemovedCorrectly() {
        lruCache.put(0)

        lruCache.remove(0)

        assertFalse(lruCache.contains(0))

    }

    @Test
    @Throws(Exception::class)
    fun testContainsReturnsCorrectlyWhenContained() {
        lruCache.put(0)

        assertTrue(lruCache.contains(0))
    }

    @Test
    @Throws(Exception::class)
    fun testContainsReturnsCorrectlyWhenNotContained() {
        assertFalse(lruCache.contains(0))
    }

    @Test
    @Throws(Exception::class)
    fun testClear() {
        lruCache.put(0)
        lruCache.put(1)
        lruCache.put(2)

        lruCache.clear()

        assertFalse(lruCache.contains(0))
        assertFalse(lruCache.contains(1))
        assertFalse(lruCache.contains(2))
    }

}