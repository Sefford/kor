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
package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class LruRepositoryTest {

    Repository<Integer, TestElement> repository;
    Repository<Integer, TestElement> lruRepository;

    @Before
    public void setUp() throws Exception {
        repository = new MemoryDataSource<>(new HashMap<Integer, TestElement>());
        lruRepository = new LruRepository<>(repository, 3);
    }

    @Test
    public void testSingleSave() throws Exception {
        lruRepository.save(new TestElement(0));

        contains(0);
    }

    @Test
    public void testLruCapabilities() throws Exception {
        lruRepository.save(new TestElement(0));
        lruRepository.save(new TestElement(1));
        lruRepository.save(new TestElement(2));
        lruRepository.save(new TestElement(3));


        doesNotContain(0);
        contains(1);
        contains(2);
        contains(3);
    }

    @Test
    public void testContainsWhenIsContained() throws Exception {
        lruRepository.save(new TestElement(0));

        contains(0);
    }

    @Test
    public void testDoesContainWhenIsNotContained() throws Exception {
        doesNotContain(0);
    }

    @Test
    public void testDoesContainWhenTheUnderlyingRepoDenies() throws Exception {
        lruRepository.save(new TestElement(0));
        repository.delete(0, null);

        doesNotContain(0);
    }

    @Test
    public void testDeletion() throws Exception {
        lruRepository.save(new TestElement(0));
        lruRepository.delete(0, null);

        doesNotContain(0);
    }

    @Test
    public void testRetrieval() throws Exception {
        TestElement originalElement = new TestElement(0);
        lruRepository.save(originalElement);

        TestElement testElement = lruRepository.get(0);
        assertThat(testElement, is(originalElement));
    }

    @Test
    public void testRetrievalWhenTheElementIsNotPersisted() throws Exception {
        TestElement originalElement = new TestElement(0);
        lruRepository.save(originalElement);
        repository.delete(0, null);

        TestElement testElement = lruRepository.get(0);
        assertThat(testElement, is(nullValue()));
    }

    @Test
    public void testClearing() throws Exception {
        lruRepository.save(new TestElement(0));
        lruRepository.save(new TestElement(1));

        lruRepository.clear();

        doesNotContain(0);
        doesNotContain(1);
    }

    @Test
    public void testAvailabilityWithInitializedLru() throws Exception {
        assertTrue(lruRepository.isAvailable());
    }

    void contains(int id) {
        assertTrue(repository.contains(id));
        assertTrue(lruRepository.contains(id));
    }

    void doesNotContain(int id) {
        assertFalse(repository.contains(id));
        assertFalse(lruRepository.contains(id));
    }
}