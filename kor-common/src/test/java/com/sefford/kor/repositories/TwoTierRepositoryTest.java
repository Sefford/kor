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
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class TwoTierRepositoryTest {
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
    Repository<Integer, TestElement> currentLevel;
    @Mock
    Repository<Integer, TestElement> nextLevel;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private List<Integer> ids = new ArrayList<Integer>();

    private TwoTierRepository<Integer, TestElement> repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);


        when(mockedElement1.getId()).thenReturn(EXPECTED_FIRST_ID);
        when(mockedElement2.getId()).thenReturn(EXPECTED_SECOND_ID);
        when(mockedElement3.getId()).thenReturn(EXPECTED_THIRD_ID);

        when(mockedElement1.update(mockedElement1)).thenReturn(mockedElement1);
        when(mockedElement2.update(mockedElement2)).thenReturn(mockedElement2);
        when(mockedElement3.update(mockedElement3)).thenReturn(mockedElement3);

        when(currentLevel.save(mockedElement1)).thenReturn(mockedElement1);
        when(currentLevel.save(mockedElement2)).thenReturn(mockedElement2);
        when(currentLevel.save(mockedElement3)).thenReturn(mockedElement3);

        elements.add(mockedElement1);
        elements.add(mockedElement2);
        elements.add(mockedElement3);

        ids.add(EXPECTED_FIRST_ID);
        ids.add(EXPECTED_SECOND_ID);
        ids.add(EXPECTED_THIRD_ID);

        repository = spy(new TwoTierRepository(currentLevel, nextLevel));

        doReturn(Boolean.FALSE).when(repository).hasNextLevel();
    }

    @Test
    public void testHasNextLevelNoNextLevel() throws Exception {
        doCallRealMethod().when(repository).hasNextLevel();

        assertFalse(repository.hasNextLevel());
    }

    @Test
    public void testHasNextLevelNextLevelNotAvailable() throws Exception {
        repository = new TwoTierRepository(currentLevel, null);
        assertFalse(repository.hasNextLevel());
    }

    @Test
    public void testHasNextLevelNextLevel() throws Exception {
        repository = new TwoTierRepository(currentLevel, nextLevel);
        when(nextLevel.isAvailable()).thenReturn(Boolean.TRUE);
        assertTrue(repository.hasNextLevel());
    }

    @Test
    public void testSaveNoNextLevel() throws Exception {
        assertEquals(mockedElement1, repository.save(mockedElement1));
        verify(nextLevel, never()).save((TestElement) any());
    }

    @Test
    public void testSaveNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        assertEquals(mockedElement1, repository.save(mockedElement1));
        verify(nextLevel, times(1)).save(mockedElement1);
    }

    @Test
    public void testSaveAllNoNextLevel() throws Exception {
        assertEquals(elements.size(), repository.saveAll(elements).size());
        verify(nextLevel, never()).saveAll(anyCollection());
    }

    @Test
    public void testSaveAllNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        assertEquals(elements.size(), repository.saveAll(elements).size());
        verify(nextLevel, times(1)).saveAll(anyCollection());
    }


    @Test
    public void testContainsNotInCurrentNoNextLevel() throws Exception {
        assertFalse(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testContainsNotInCurrentNoInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        assertFalse(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testContainsNotInCurrentInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(nextLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testContainsInCurrentNoNextLevel() throws Exception {
        when(currentLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testContainsInCurrentNoInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(currentLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testContainsInCurrentInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(currentLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        when(nextLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
    }


    @Test
    public void testDeleteNoNextLevel() throws Exception {
        repository.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(currentLevel, times(1)).delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(nextLevel, never()).delete(anyInt(), (TestElement) any());
    }

    @Test
    public void testDeleteNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        repository.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(currentLevel, times(1)).delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(nextLevel, times(1)).delete(anyInt(), (TestElement) any());
    }

    @Test
    public void testDeleteAllNoNextLevel() throws Exception {
        repository.deleteAll(elements);
        verify(currentLevel, times(1)).deleteAll(elements);
        verify(nextLevel, never()).deleteAll(elements);
    }

    @Test
    public void testDeleteAllNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        repository.deleteAll(elements);
        verify(currentLevel, times(1)).deleteAll(elements);
        verify(nextLevel, times(1)).deleteAll(elements);
    }

    @Test
    public void testGetNotCurrentLevelNoNextLevel() throws Exception {
        assertNull(repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testGetNotCurrentLevelNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        assertNull(repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testGetNotCurrentLevelInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(nextLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);

        assertEquals(mockedElement1, repository.get(EXPECTED_FIRST_ID));
        verify(currentLevel, times(1)).save(mockedElement1);
    }

    @Test
    public void testGetInCurrentLevelNoNextLevel() throws Exception {
        when(currentLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        when(currentLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);

        assertEquals(mockedElement1, repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testGetInCurrentLevelNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(nextLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);

        assertEquals(mockedElement1, repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testGetInCurrentLevelInNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();
        when(nextLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        when(nextLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);

        assertEquals(mockedElement1, repository.get(EXPECTED_FIRST_ID));
        verify(currentLevel, times(1)).save(mockedElement1);
    }

    @Test
    public void testClearNoNextLevel() throws Exception {
        repository.clear();
        verify(currentLevel, times(1)).clear();
        verify(nextLevel, never()).clear();
    }

    @Test
    public void testClearNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        repository.clear();
        verify(currentLevel, times(1)).clear();
        verify(nextLevel, times(1)).clear();
    }

    @Test
    public void testGetAllNoNextLevel() throws Exception {
        repository.getAll();
        verify(currentLevel, atLeastOnce()).getAll();
        verify(nextLevel, never()).getAll();
    }

    @Test
    public void testGetAllNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        repository.getAll();
        verify(currentLevel, atLeastOnce()).getAll();
        verify(nextLevel, atLeastOnce()).getAll();
    }

    @Test
    public void testGetAllIdsNullElements() throws Exception {
        when(currentLevel.contains(anyInt())).thenReturn(Boolean.TRUE);

        repository.getAll(ids);

        verify(currentLevel, atLeastOnce()).get(anyInt());
    }

    @Test
    public void testIsAvailableNullCurrent() throws Exception {
        repository = new TwoTierRepository(null, null);

        assertFalse(repository.isAvailable());
    }

    @Test
    public void testIsAvailableNotAvailable() throws Exception {
        assertFalse(repository.isAvailable());
    }

    @Test
    public void testIsAvailable() throws Exception {
        when(currentLevel.isAvailable()).thenReturn(Boolean.TRUE);
        assertTrue(repository.isAvailable());
    }

    @Test
    public void testThreeTierResolvesGetCorrectly() throws Exception {
        final Repository<Integer, TestElement> firstLevel = mock(Repository.class);
        final Repository<Integer, TestElement> secondLevel = mock(Repository.class);
        final Repository<Integer, TestElement> thirdLevel = mock(Repository.class);

        final TwoTierRepository<Integer, TestElement> localLevel = new TwoTierRepository(firstLevel, secondLevel);
        final TwoTierRepository<Integer, TestElement> repository = new TwoTierRepository(localLevel, thirdLevel);

        when(firstLevel.get(EXPECTED_FIRST_ID)).thenReturn(null);
        when(firstLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(secondLevel.isAvailable()).thenReturn(Boolean.FALSE);
        when(thirdLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(thirdLevel.get(EXPECTED_FIRST_ID)).thenReturn(mock(TestElement.class));

        assertNotNull(repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testThreeTierResolvesContainsCorrectly() throws Exception {
        final Repository<Integer, TestElement> firstLevel = mock(Repository.class);
        final Repository<Integer, TestElement> secondLevel = mock(Repository.class);
        final Repository<Integer, TestElement> thirdLevel = mock(Repository.class);

        final TwoTierRepository<Integer, TestElement> localLevel = new TwoTierRepository(firstLevel, secondLevel);
        final TwoTierRepository<Integer, TestElement> repository = new TwoTierRepository(localLevel, thirdLevel);

        when(firstLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.FALSE);
        when(firstLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(secondLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.FALSE);
        when(secondLevel.isAvailable()).thenReturn(Boolean.FALSE);
        when(thirdLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(thirdLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testTierOneDoesNotContainElementTierTwoFailsAndTierThreeResolves() {
        final Repository<Integer, TestElement> firstLevel = mock(Repository.class);
        final Repository<Integer, TestElement> secondLevel = mock(Repository.class);
        final Repository<Integer, TestElement> thirdLevel = mock(Repository.class);

        final TwoTierRepository<Integer, TestElement> localLevel = new TwoTierRepository(firstLevel, secondLevel);
        final TwoTierRepository<Integer, TestElement> repository = new TwoTierRepository(localLevel, thirdLevel);

        when(firstLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.FALSE);
        when(firstLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(firstLevel.get(EXPECTED_FIRST_ID)).thenReturn(null);
        when(secondLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        when(secondLevel.isAvailable()).thenReturn(Boolean.FALSE);
        when(secondLevel.get(EXPECTED_FIRST_ID)).thenReturn(null);
        when(thirdLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(thirdLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        when(thirdLevel.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);

        assertThat(repository.get(EXPECTED_FIRST_ID), is(mockedElement1));
    }
}