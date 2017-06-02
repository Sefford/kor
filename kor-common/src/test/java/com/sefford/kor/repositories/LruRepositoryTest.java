package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.interfaces.Updateable;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by sefford on 6/2/17.
 */
public class LruRepositoryTest {

    Repository<Integer, TestElement> repository;
    Repository<Integer, TestElement> lruRepository;

    @Before
    public void setUp() throws Exception {
        repository = new MemoryRepository<>(new HashMap<Integer, TestElement>());
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


    class TestElement implements RepoElement<Integer>, Updateable<TestElement> {

        final int id;

        TestElement(int id) {
            this.id = id;
        }


        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public TestElement update(TestElement other) {
            return this;
        }

        @Override
        public boolean equals(Object that) {
            return id == ((TestElement) that).id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }


}