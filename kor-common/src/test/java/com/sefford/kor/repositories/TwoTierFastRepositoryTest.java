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

import com.sefford.kor.repositories.interfaces.FastRepository;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class TwoTierFastRepositoryTest {
    private static final int EXPECTED_FIRST_ID = 1;
    private static final int EXPECTED_SECOND_ID = 2;
    private static final int EXPECTED_THIRD_ID = 3;

    @Mock
    private TestElement mockedElement1;
    @Mock
    private TestElement mockedElement2;
    @Mock
    private TestElement mockedElement3;
    @Mock
    private TestRepository currentLevel;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private List<Integer> ids = new ArrayList<>();

    private TwoTierFastRepository<Integer, TestElement> repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        elements.add(mockedElement1);
        elements.add(mockedElement2);
        elements.add(mockedElement3);

        ids.add(EXPECTED_FIRST_ID);
        ids.add(EXPECTED_SECOND_ID);
        ids.add(EXPECTED_THIRD_ID);

        repository = new TwoTierFastRepository<Integer, TestElement>(currentLevel, null) {
        };
    }

    @Test
    public void testContainsInMemory() throws Exception {
        repository.containsInMemory(EXPECTED_FIRST_ID);
        verify(currentLevel, times(1)).containsInMemory(EXPECTED_FIRST_ID);
    }

    @Test
    public void testGetFromMemory() throws Exception {
        repository.getFromMemory(EXPECTED_FIRST_ID);
        verify(currentLevel, times(1)).getFromMemory(EXPECTED_FIRST_ID);
    }

    @Test
    public void testGetAllFromMemory() throws Exception {
        repository.getAllFromMemory(ids);
        verify(currentLevel, times(1)).getAllFromMemory(ids);
    }

    @Test
    public void testSaveInMemory() throws Exception {
        repository.saveInMemory(mockedElement1);
        verify(currentLevel, times(1)).saveInMemory(mockedElement1);
    }

    @Test
    public void testSaveAllInMemory() throws Exception {
        repository.saveAllInMemory(elements);
        verify(currentLevel, times(1)).saveAllInMemory(elements);
    }

    class TestRepository implements Repository<Integer, TestElement>, FastRepository<Integer, TestElement> {

        @Override
        public boolean containsInMemory(Integer id) {
            return false;
        }

        @Override
        public TestElement getFromMemory(Integer id) {
            return null;
        }

        @Override
        public Collection<TestElement> getAllFromMemory(List<Integer> ids) {
            return null;
        }

        @Override
        public TestElement saveInMemory(TestElement element) {
            return null;
        }

        @Override
        public Collection<TestElement> saveAllInMemory(Collection<TestElement> elements) {
            return null;
        }

        @Override
        public void clear() {

        }

        @Override
        public boolean contains(Integer id) {
            return false;
        }

        @Override
        public void delete(Integer id, TestElement element) {

        }

        @Override
        public void deleteAll(List<TestElement> elements) {

        }

        @Override
        public TestElement get(Integer id) {
            return null;
        }

        @Override
        public Collection<TestElement> getAll(Collection<Integer> ids) {
            return null;
        }

        @Override
        public Collection<TestElement> getAll() {
            return null;
        }

        @Override
        public TestElement save(TestElement element) {
            return null;
        }

        @Override
        public Collection<TestElement> saveAll(Collection<TestElement> elements) {
            return null;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }
    }
}