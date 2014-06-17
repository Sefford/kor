package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for Base Repository
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class BaseRepositoryTest {

    @Mock
    Repository mockedRepo;

    BaseRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        repository = createRepo(mockedRepo);
    }

    @Test
    public void testHasNextLevelWithoutNextLevel() throws Exception {
        repository = createRepo(null);
        assertThat(repository.hasNextLevel(), equalTo(Boolean.FALSE));
    }

    @Test
    public void testHasNextLevelNextLevelUnavailable() throws Exception {
        when(mockedRepo.isAvailable()).thenReturn(Boolean.FALSE);
        assertThat(repository.hasNextLevel(), equalTo(Boolean.FALSE));
    }

    @Test
    public void testHasNextLeve() throws Exception {
        when(mockedRepo.isAvailable()).thenReturn(Boolean.TRUE);
        assertThat(repository.hasNextLevel(), equalTo(Boolean.TRUE));
    }

    private BaseRepository createRepo(final Repository nextLevel) {
        return new BaseRepository(nextLevel) {
            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object id) {
                return false;
            }

            @Override
            public void delete(Object id, RepoElement element) {

            }

            @Override
            public void deleteAll(List elements) {

            }

            @Override
            public RepoElement get(Object id) {
                return null;
            }

            @Override
            public Collection getAll() {
                return null;
            }

            @Override
            public RepoElement save(RepoElement element) {
                return null;
            }

            @Override
            public Collection saveAll(Collection elements) {
                return null;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }
        };
    }
}