package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.interfaces.Updateable;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for Memory Repository
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class MemoryRepositoryTest {

    private static final long EXPECTED_FIRST_ID = 1;
    private static final long EXPECTED_SECOND_ID = 2;
    private static final long EXPECTED_THIRD_ID = 3;
    @Mock
    private Map<Long, TestElement> map;
    @Mock
    private Repository<Long, TestElement> mockedRepository;
    @Mock
    private TestElement mockedElement1;
    @Mock
    private TestElement mockedElement2;
    @Mock
    private TestElement mockedElement3;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private TestMemoryRepository repo;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockedElement1.getId()).thenReturn(EXPECTED_FIRST_ID);
        when(mockedElement2.getId()).thenReturn(EXPECTED_SECOND_ID);
        when(mockedElement3.getId()).thenReturn(EXPECTED_THIRD_ID);

        elements.add(mockedElement1);
        elements.add(mockedElement2);
        elements.add(mockedElement3);

        when(map.values()).thenReturn(elements);

        when(mockedRepository.isAvailable()).thenReturn(Boolean.TRUE);
        repo = spy(new TestMemoryRepository(null, map));
    }

    @Test
    public void testSaveNotExistingElement() throws Exception {
        assertThat(repo.save(mockedElement1), equalTo(mockedElement1));
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(mockedElement1, times(0)).update(mockedElement1);
    }

    @Test
    public void testSaveExistingElement() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertThat(repo.save(mockedElement1), equalTo(mockedElement1));
        verify(map, times(0)).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(mockedElement1, times(1)).update(mockedElement1);
    }

    @Test
    public void testSaveNoNextLevel() throws Exception {
        repo.save(mockedElement1);
        verify(mockedRepository, times(0)).save(mockedElement1);
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
    }

    @Test
    public void testSaveNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        repo.save(mockedElement1);
        verify(mockedRepository, times(1)).save(mockedElement1);
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
    }

    @Test
    public void testSaveAllNoNextLevel() throws Exception {
        repo.saveAll(elements);
        verify(mockedRepository, times(0)).save(mockedElement1);
        verify(mockedRepository, times(0)).save(mockedElement2);
        verify(mockedRepository, times(0)).save(mockedElement3);
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).put(EXPECTED_SECOND_ID, mockedElement2);
        verify(map, times(1)).put(EXPECTED_THIRD_ID, mockedElement3);
    }

    @Test
    public void testSaveAllNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        repo.saveAll(elements);
        verify(mockedRepository, times(1)).save(mockedElement1);
        verify(mockedRepository, times(1)).save(mockedElement2);
        verify(mockedRepository, times(1)).save(mockedElement3);
        verify(map, times(1)).put(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).put(EXPECTED_SECOND_ID, mockedElement2);
        verify(map, times(1)).put(EXPECTED_THIRD_ID, mockedElement3);
    }

    @Test
    public void testContainsDoubleMiss() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        assertThat(repo.contains(EXPECTED_FIRST_ID), equalTo(Boolean.FALSE));
    }

    @Test
    public void testContainsMemoryMissWithNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        when(mockedRepository.contains(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        assertThat(repo.contains(EXPECTED_FIRST_ID), equalTo(Boolean.TRUE));
    }

    @Test
    public void testContainsMemoryMissWithNoNextLevel() throws Exception {
        assertThat(repo.contains(EXPECTED_FIRST_ID), equalTo(Boolean.FALSE));
    }

    @Test
    public void testContainsDirectHit() throws Exception {
        when(map.containsKey(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        assertThat(repo.contains(EXPECTED_FIRST_ID), equalTo(Boolean.TRUE));
    }

    @Test
    public void testDeleteNoNextLevel() throws Exception {
        repo.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
        verify(mockedRepository, times(0)).delete(EXPECTED_FIRST_ID, mockedElement1);
    }

    @Test
    public void testDeleteWithNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        repo.delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
        verify(mockedRepository, times(1)).delete(EXPECTED_FIRST_ID, mockedElement1);
    }

    @Test
    public void testDeleteAllNoNextLevel() throws Exception {
        repo.deleteAll(elements);
        verify(mockedRepository, times(0)).delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(mockedRepository, times(0)).delete(EXPECTED_SECOND_ID, mockedElement2);
        verify(mockedRepository, times(0)).delete(EXPECTED_THIRD_ID, mockedElement3);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
        verify(map, times(1)).remove(EXPECTED_SECOND_ID);
        verify(map, times(1)).remove(EXPECTED_THIRD_ID);
    }

    @Test
    public void testDeleteAllWithNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        repo.deleteAll(elements);
        verify(mockedRepository, times(1)).delete(EXPECTED_FIRST_ID, mockedElement1);
        verify(mockedRepository, times(1)).delete(EXPECTED_SECOND_ID, mockedElement2);
        verify(mockedRepository, times(1)).delete(EXPECTED_THIRD_ID, mockedElement3);
        verify(map, times(1)).remove(EXPECTED_FIRST_ID);
        verify(map, times(1)).remove(EXPECTED_SECOND_ID);
        verify(map, times(1)).remove(EXPECTED_THIRD_ID);
    }

    @Test
    public void testGetDoubleMiss() throws Exception {
        assertThat(repo.get(EXPECTED_FIRST_ID), nullValue());
    }

    @Test
    public void testGetMemoryMissWithNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        when(mockedRepository.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertThat(repo.get(EXPECTED_FIRST_ID), equalTo(mockedElement1));
    }

    @Test
    public void testGetMemoryMissWithNoNextLevel() throws Exception {
        assertThat(repo.get(EXPECTED_FIRST_ID), nullValue());
    }


    @Test
    public void testGetDirectHit() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertThat(repo.get(EXPECTED_FIRST_ID), equalTo(mockedElement1));
    }

    @Test
    public void testClearNoNextLevel() throws Exception {
        repo.clear();
        verify(map, times(1)).clear();
    }

    @Test
    public void testClearWithNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, map);
        repo.clear();
        verify(map, times(1)).clear();
        verify(mockedRepository, times(1)).clear();
    }

    @Test
    public void testContainsInMemoryNotInMemory() throws Exception {
        assertThat(repo.containsInMemory(EXPECTED_FIRST_ID), equalTo(Boolean.FALSE));
    }

    @Test
    public void testContainsInMemory() throws Exception {
        when(map.containsKey(EXPECTED_FIRST_ID)).thenReturn(Boolean.TRUE);
        assertThat(repo.containsInMemory(EXPECTED_FIRST_ID), equalTo(Boolean.TRUE));
    }

    @Test
    public void testHasNextLevelNoNextLevel() throws Exception {
        assertThat(repo.hasNextLevel(), equalTo(Boolean.FALSE));
    }

    @Test
    public void testHasNextLevelNextLevelNotAvailable() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, new HashMap<Long, TestElement>());
        when(mockedRepository.isAvailable()).thenReturn(Boolean.FALSE);
        assertThat(repo.hasNextLevel(), equalTo(Boolean.FALSE));
    }

    @Test
    public void testHasNextLevel() throws Exception {
        repo = new TestMemoryRepository(mockedRepository, new HashMap<Long, TestElement>());
        when(mockedRepository.isAvailable()).thenReturn(Boolean.TRUE);
        assertThat(repo.hasNextLevel(), equalTo(Boolean.TRUE));
    }


    @Test
    public void testGetAll() throws Exception {
        assertThat(repo.getAll(), IsEqual.<Collection<TestElement>>equalTo(elements));
    }

    @Test
    public void testIsAvailable() throws Exception {
        assertThat(repo.isAvailable(), equalTo(Boolean.TRUE));
    }

    @Test
    public void testIsAvailableNotAvailable() throws Exception {
        repo = new TestMemoryRepository(null, null);
        assertThat(repo.isAvailable(), equalTo(Boolean.FALSE));
    }

    @Test
    public void testGetFromMemory() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        assertThat(repo.getFromMemory(EXPECTED_FIRST_ID), equalTo(mockedElement1));
    }

    @Test
    public void testGetFromMemoryWithNextLevel() throws Exception {
        when(map.get(EXPECTED_FIRST_ID)).thenReturn(mockedElement1);
        repo = new TestMemoryRepository(mockedRepository, map);
        assertThat(repo.getFromMemory(EXPECTED_FIRST_ID), equalTo(mockedElement1));
        verifyZeroInteractions(mockedRepository);
    }

    class TestMemoryRepository extends MemoryRepository<Long, TestElement> {

        /**
         * Creates a new instance of Memory repository
         *
         * @param nextLevel Repository to provide the next level of caching
         * @param cache     Storage map of the repository
         */
        protected TestMemoryRepository(Repository<Long, TestElement> nextLevel, Map<Long, TestElement> cache) {
            super(nextLevel, cache);
        }
    }

    class TestElement implements RepoElement<Long>, Updateable<TestElement> {

        @Override
        public Long getId() {
            return null;
        }

        @Override
        public TestElement update(TestElement other) {
            return null;
        }
    }
}
