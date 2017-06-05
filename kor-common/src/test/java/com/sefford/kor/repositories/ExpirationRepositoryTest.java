package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.ExpirationPolicy;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sefford on 6/5/17.
 */
public class ExpirationRepositoryTest {

    Repository<Integer, TestElement> repository;
    Repository<Integer, TestElement> expirationRepo;

    @Mock
    ExpirationPolicy<Integer> policy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        repository = new MemoryRepository<>(new HashMap<Integer, TestElement>());
        expirationRepo = new ExpirationRepository<>(repository, policy);
    }

    @Test
    public void testCorrectClearing() throws Exception {
        expirationRepo.save(new TestElement(0));
        expirationRepo.save(new TestElement(1));
        expirationRepo.save(new TestElement(2));

        expirationRepo.clear();

        verify(policy, times(1)).clear();
        assertFalse(repository.contains(0));
        assertFalse(repository.contains(1));
        assertFalse(repository.contains(2));
    }

    @Test
    public void testContainsWhenExpired() throws Exception {
        expirationRepo.save(new TestElement(0));
        when(policy.isExpired(0)).thenReturn(Boolean.TRUE);

        assertFalse(expirationRepo.contains(0));
        verify(policy, times(1)).notifyDeleted(0);
    }

    @Test
    public void testContainsWhenNotExpiredAndInInnerRepo() throws Exception {
        expirationRepo.save(new TestElement(0));
        when(policy.isExpired(0)).thenReturn(Boolean.FALSE);

        assertTrue(expirationRepo.contains(0));
        verify(policy, never()).notifyDeleted(0);
    }

    @Test
    public void testContainsWhenNotExpiredAndNotInInnerRepo() throws Exception {
        when(policy.isExpired(0)).thenReturn(Boolean.FALSE);

        assertFalse(expirationRepo.contains(0));
        verify(policy, never()).notifyDeleted(0);
    }

    @Test
    public void testDeletion() throws Exception {
        expirationRepo.save(new TestElement(0));

        expirationRepo.delete(0, null);

        verify(policy, times(1)).notifyDeleted(0);
        assertFalse(repository.contains(0));
    }

    @Test
    public void testRetrievalWhenNotExpired() throws Exception {
        expirationRepo.save(new TestElement(0));
        when(policy.isExpired(0)).thenReturn(Boolean.FALSE);

        TestElement testElement = expirationRepo.get(0);

        assertNotNull(testElement);
        assertThat(testElement.getId(), is(0));
    }

    @Test
    public void testRetrievalWhenExpired() throws Exception {
        expirationRepo.save(new TestElement(0));
        when(policy.isExpired(0)).thenReturn(Boolean.TRUE);

        TestElement testElement = expirationRepo.get(0);

        assertNull(testElement);
    }

    @Test
    public void testGetPartialRetrievalWithExpiredElements() throws Exception {
        expirationRepo.save(new TestElement(0));
        expirationRepo.save(new TestElement(1));
        expirationRepo.save(new TestElement(2));

        when(policy.isExpired(1)).thenReturn(Boolean.TRUE);

        final Collection<TestElement> elements = expirationRepo.getAll(Arrays.asList(0, 1, 2));

        assertTrue(elements.contains(new TestElement(0)));
        assertTrue(elements.contains(new TestElement(2)));
    }

    @Test
    public void testFullRetrievalWithExpiredElements() throws Exception {
        expirationRepo.save(new TestElement(0));
        expirationRepo.save(new TestElement(1));
        expirationRepo.save(new TestElement(2));

        when(policy.isExpired(1)).thenReturn(Boolean.TRUE);

        final Collection<TestElement> elements = expirationRepo.getAll();

        assertTrue(elements.contains(new TestElement(0)));
        assertTrue(elements.contains(new TestElement(2)));
    }

    @Test
    public void testSaving() throws Exception {
        expirationRepo.save(new TestElement(0));

        verify(policy, times(1)).notifyCreated(0);
        assertTrue(expirationRepo.contains(0));
    }

    @Test
    public void testAvailabilityWhenPolicyIsNull() throws Exception {
        expirationRepo = new ExpirationRepository<>(repository, null);

        assertFalse(expirationRepo.isAvailable());
    }

    @Test
    public void testAvailabilityWhenRepositoryIsNull() throws Exception {
        expirationRepo = new ExpirationRepository<>(null, policy);

        assertFalse(expirationRepo.isAvailable());
    }

    @Test
    public void testAvailabilityWhenTheRepoIsWorking() throws Exception {
        assertTrue(expirationRepo.isAvailable());
    }
}