package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.interfaces.Updateable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseRepositoryTest {
    private static final long EXPECTED_FIRST_ID = 1;
    private static final long EXPECTED_SECOND_ID = 2;
    private static final long EXPECTED_THIRD_ID = 3;

    @Mock
    private TestElement mockedElement1;
    @Mock
    private TestElement mockedElement2;
    @Mock
    private TestElement mockedElement3;
    @Mock
    Repository<Long, TestElement> currentLevel;
    @Mock
    Repository<Long, TestElement> nextLevel;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private List<Long> ids = new ArrayList<Long>();

    private BaseRepository<Long, TestElement> repository;

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

        repository = spy(new BaseRepositoryImpl(currentLevel, nextLevel));

        doReturn(Boolean.FALSE).when(repository).hasNextLevel();
    }

    @Test
    public void testHasNextLevelNoNextLevel() throws Exception {
        doCallRealMethod().when(repository).hasNextLevel();

        assertFalse(repository.hasNextLevel());
    }

    @Test
    public void testHasNextLevelNextLevelNotAvailable() throws Exception {
        repository = new BaseRepositoryImpl(currentLevel, null);
        assertFalse(repository.hasNextLevel());
    }

    @Test
    public void testHasNextLevelNextLevel() throws Exception {
        repository = new BaseRepositoryImpl(currentLevel, nextLevel);
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
        verify(nextLevel, never()).delete(anyLong(), (TestElement) any());
    }

    @Test
    public void testDeleteNextLevel() throws Exception {
        doReturn(Boolean.TRUE).when(repository).hasNextLevel();

        repository.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(currentLevel, times(1)).delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(nextLevel, times(1)).delete(anyLong(), (TestElement) any());
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
        repository.getAll(ids);
        verify(currentLevel, atLeastOnce()).get(anyLong());
    }

    @Test
    public void testIsAvailableNullCurrent() throws Exception {
        repository = new BaseRepositoryImpl(null, null);

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
        final Repository<Long, TestElement> firstLevel = mock(Repository.class);
        final Repository<Long, TestElement> secondLevel = mock(Repository.class);
        final Repository<Long, TestElement> thirdLevel = mock(Repository.class);

        final BaseRepository<Long, TestElement> localLevel = new BaseRepositoryImpl(firstLevel, secondLevel);
        final BaseRepository<Long, TestElement> repository = new BaseRepositoryImpl(localLevel, thirdLevel);

        when(firstLevel.get(EXPECTED_FIRST_ID)).thenReturn(null);
        when(firstLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(secondLevel.isAvailable()).thenReturn(Boolean.FALSE);
        when(thirdLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(thirdLevel.get(EXPECTED_FIRST_ID)).thenReturn(mock(TestElement.class));

        assertNotNull(repository.get(EXPECTED_FIRST_ID));
    }

    @Test
    public void testThreeTierResolvesContainsCorrectly() throws Exception {
        final Repository<Long, TestElement> firstLevel = mock(Repository.class);
        final Repository<Long, TestElement> secondLevel = mock(Repository.class);
        final Repository<Long, TestElement> thirdLevel = mock(Repository.class);

        final BaseRepository<Long, TestElement> localLevel = new BaseRepositoryImpl(firstLevel, secondLevel);
        final BaseRepository<Long, TestElement> repository = new BaseRepositoryImpl(localLevel, thirdLevel);

        when(firstLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.FALSE);
        when(firstLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(secondLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.FALSE);
        when(secondLevel.isAvailable()).thenReturn(Boolean.FALSE);
        when(thirdLevel.isAvailable()).thenReturn(Boolean.TRUE);
        when(thirdLevel.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);

        assertTrue(repository.contains(EXPECTED_FIRST_ID));
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

    class BaseRepositoryImpl extends BaseRepository<Long, TestElement> {

        /**
         * Creates a new instance of a BaseRepository with next level.
         * <p/>
         * This next level can be optionally initialized to null.
         *
         * @param currentLevel Current Level of the Repository
         * @param nextLevel    Next Level of the Repository
         */
        protected BaseRepositoryImpl(Repository<Long, TestElement> currentLevel, Repository<Long, TestElement> nextLevel) {
            super(currentLevel, nextLevel);
        }
    }

}