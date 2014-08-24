package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.FastRepository;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.interfaces.Updateable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseFastRepositoryTest {
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
    private TestRepository currentLevel;

    private List<TestElement> elements = new ArrayList<TestElement>();
    private List<Long> ids = new ArrayList<Long>();

    private BaseFastRepository<Long, TestElement> repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        elements.add(mockedElement1);
        elements.add(mockedElement2);
        elements.add(mockedElement3);

        ids.add(EXPECTED_FIRST_ID);
        ids.add(EXPECTED_SECOND_ID);
        ids.add(EXPECTED_THIRD_ID);

        repository = new BaseFastRepositoryImpl(currentLevel, null);
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

    class TestRepository implements Repository<Long, TestElement>, FastRepository<Long, TestElement> {

        @Override
        public boolean containsInMemory(Long id) {
            return false;
        }

        @Override
        public TestElement getFromMemory(Long id) {
            return null;
        }

        @Override
        public Collection<TestElement> getAllFromMemory(List<Long> ids) {
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
        public boolean contains(Long id) {
            return false;
        }

        @Override
        public void delete(Long id, TestElement element) {

        }

        @Override
        public void deleteAll(List<TestElement> elements) {

        }

        @Override
        public TestElement get(Long id) {
            return null;
        }

        @Override
        public Collection<TestElement> getAll(Collection<Long> ids) {
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

    class BaseFastRepositoryImpl extends BaseFastRepository<Long, TestElement> {

        /**
         * Creates a new instance of a BaseRepository with next level.
         * <p/>
         * This next level can be optionally initialized to null.
         *
         * @param currentLevel Current Level of the Repository
         * @param nextLevel    Next Level of the Repository
         */
        protected BaseFastRepositoryImpl(FastRepository<Long, TestElement> currentLevel, Repository<Long, TestElement> nextLevel) {
            super(currentLevel, nextLevel);
        }
    }
}