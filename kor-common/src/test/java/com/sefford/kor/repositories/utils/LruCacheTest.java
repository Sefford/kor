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
package com.sefford.kor.repositories.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class LruCacheTest {

    LruCache<Integer> lruCache;

    @Before
    public void setUp() throws Exception {
        lruCache = new LruCache<>(3);
    }

    @Test
    public void testPutCorrectlyWhenNotInLru() throws Exception {
        final Integer previous = lruCache.put(0);

        assertNull(previous);
        assertTrue(lruCache.contains(0));
    }

    @Test
    public void testPutSameValueIsCorrectlyRefreshed() throws Exception {
        lruCache.put(0);
        lruCache.put(1);
        lruCache.put(2);
        final Integer previous = lruCache.put(0);

        assertNull(previous);
        final Iterator itr = lruCache.values.iterator();
        assertThat(itr.next(), is(1));
        assertThat(itr.next(), is(2));
        assertThat(itr.next(), is(0));
    }

    @Test
    public void testPutNull() throws Exception {
        Integer previous = lruCache.put(null);

        assertNull(previous);
        assertTrue(lruCache.values.isEmpty());
    }

    @Test
    public void testOrderIsRespected() throws Exception {
        lruCache.put(0);
        lruCache.put(1);
        lruCache.put(2);

        final Iterator itr = lruCache.values.iterator();
        assertThat(itr.next(), is(0));
        assertThat(itr.next(), is(1));
        assertThat(itr.next(), is(2));
    }

    @Test
    public void testItemsAreProperlyEvicted() throws Exception {
        lruCache.put(0);
        lruCache.put(1);
        lruCache.put(2);
        final Integer previous = lruCache.put(3);

        assertThat(previous, is(0));
        final Iterator itr = lruCache.values.iterator();
        assertThat(itr.next(), is(1));
        assertThat(itr.next(), is(2));
        assertThat(itr.next(), is(3));
    }

    @Test
    public void testRefreshBringsElementToFront() throws Exception {
        lruCache.put(0);
        lruCache.put(1);
        lruCache.put(2);

        lruCache.refresh(0);

        assertThat(lruCache.values.iterator().next(), is(1));
    }

    @Test
    public void testIsRemovedCorrectly() throws Exception {
        lruCache.put(0);

        lruCache.remove(0);

        assertFalse(lruCache.contains(0));

    }

    @Test
    public void testContainsReturnsCorrectlyWhenContained() throws Exception {
        lruCache.put(0);

        assertTrue(lruCache.contains(0));
    }

    @Test
    public void testContainsReturnsCorrectlyWhenNotContained() throws Exception {
        assertFalse(lruCache.contains(0));
    }

    @Test
    public void testClear() throws Exception {
        lruCache.put(0);
        lruCache.put(1);
        lruCache.put(2);

        lruCache.clear();

        assertFalse(lruCache.contains(0));
        assertFalse(lruCache.contains(1));
        assertFalse(lruCache.contains(2));
    }

}