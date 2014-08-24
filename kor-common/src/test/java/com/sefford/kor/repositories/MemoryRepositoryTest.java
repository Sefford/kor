package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Updateable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MemoryRepositoryTest {
    private static final long EXPECTED_FIRST_ID = 1;
    private static final long EXPECTED_SECOND_ID = 2;
    private static final long EXPECTED_THIRD_ID = 3;

    @Mock
    private TestElement mockedElement1;
    @Mock
    private TestElement mockedElement2;
    @Mock
    private TestElement mockedElement3;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private List<Long> ids = new ArrayList<Long>();
    @Mock
    Map<Long, TestElement> map;

    MemoryRepository<Long, TestElement> repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockedElement1.getId()).thenReturn(EXPECTED_FIRST_ID);
        when(mockedElement2.getId()).thenReturn(EXPECTED_SECOND_ID);
        when(mockedElement3.getId()).thenReturn(EXPECTED_THIRD_ID);

        when(map.put(EXPECTED_FIRST_ID, mockedElement1)).thenReturn(mockedElement1);
        when(map.put(EXPECTED_SECOND_ID, mockedElement2)).thenReturn(mockedElement2);
        when(map.put(EXPECTED_THIRD_ID, mockedElement3)).thenReturn(mockedElement3);

        when(mockedElement1.update(mockedElement1)).thenReturn(mockedElement1);
        when(mockedElement2.update(mockedElement2)).thenReturn(mockedElement2);
        when(mockedElement3.update(mockedElement3)).thenReturn(mockedElement3);

        elements.add(mockedElement1);
        elements.add(mockedElement2);
        elements.add(mockedElement3);

        ids.add(EXPECTED_FIRST_ID);
        ids.add(EXPECTED_SECOND_ID);
        ids.add(EXPECTED_THIRD_ID);

        repository = spy(new MemoryRepository<Long, TestElement>(map));
    }

    @Test
    public void testSaveWithNoExistingElement() throws Exception {
        assertEquals(mockedElement1, repository.save(mockedElement1));
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
    }

    @Test
    public void testSaveWithExistingElement() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertEquals(mockedElement1, repository.save(mockedElement1));
        verify(map, never()).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(mockedElement1, times(1)).update(mockedElement1);
    }


    @Test
    public void testSaveAll() throws Exception {
        assertEquals(elements, repository.saveAll(elements));
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).put(EXPECTED_SECOND_ID, mockedElement2);
        verify(map, times(1)).put(EXPECTED_THIRD_ID, mockedElement3);
    }

    @Test
    public void testContains() throws Exception {
        assertFalse(repository.contains(EXPECTED_FIRST_ID));
    }

    @Test
    public void testDelete() throws Exception {
        repository.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
    }

    @Test
    public void testDeleteAll() throws Exception {
        repository.deleteAll(elements);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
        verify(map, times(1)).remove(EXPECTED_SECOND_ID);
        verify(map, times(1)).remove(EXPECTED_THIRD_ID);
    }

    @Test
    public void testGet() throws Exception {
        when(repository.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertEquals(mockedElement1, repository.get(EXPECTED_FIRST_ID));

    }

    @Test
    public void testClear() throws Exception {
        repository.clear();
        verify(map, times(1)).clear();
    }

    @Test
    public void testGetAll() throws Exception {
        when(map.values()).thenReturn(elements);

        assertEquals(elements.size(), repository.getAll().size());
        verify(map, times(1)).values();
    }

    @Test
    public void testContainsInMemory() throws Exception {
        repository.containsInMemory(EXPECTED_FIRST_ID);
        verify(repository, times(1)).contains(EXPECTED_FIRST_ID);
    }

    @Test
    public void testGetFromMemory() throws Exception {
        repository.getFromMemory(EXPECTED_FIRST_ID);
        verify(repository, times(1)).get(EXPECTED_FIRST_ID);
    }

    @Test
    public void testGetAllFromMemory() throws Exception {
        repository.getAllFromMemory(ids);
        verify(repository, times(1)).getAll();
    }

    @Test
    public void testSaveInMemory() throws Exception {
        repository.saveInMemory(mockedElement1);
        verify(repository, times(1)).save(mockedElement1);
    }

    @Test
    public void testSaveAllInMemory() throws Exception {
        repository.saveAllInMemory(elements);
        verify(repository, times(1)).saveAll(elements);
    }

    @Test
    public void testIsAvailable() throws Exception {
        assertTrue(repository.isAvailable());
    }

    @Test
    public void testIsAvailableNotAvailable() throws Exception {
        repository = new MemoryRepository<Long, TestElement>(null);
        assertFalse(repository.isAvailable());
    }

    @Test
    public void saveGetAllPartial() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        when(map.get(EXPECTED_THIRD_ID)).thenReturn(mockedElement3);

        assertEquals(elements.size() -1 , repository.getAll(ids).size());
        verify(map, times(1)).get(EXPECTED_FIRST_ID);
        verify(map, times(1)).get(EXPECTED_SECOND_ID);
        verify(map, times(1)).get(EXPECTED_THIRD_ID);
    }

    class TestElement implements RepoElement<Long>, Updateable<TestElement> {

        @Override
        public Long getId() {
            return null;
        }

        @Override
        public TestElement update(TestElement other) {
            return this;
        }
    }

}