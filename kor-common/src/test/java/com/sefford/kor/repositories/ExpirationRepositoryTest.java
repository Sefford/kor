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

import com.sefford.kor.repositories.interfaces.ExpirationPolicy;
import com.sefford.kor.repositories.interfaces.Repository;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class ExpirationRepositoryTest {

    Repository<Integer, TestElement> repository;
    Repository<Integer, TestElement> expirationRepo;

    @Mock
    ExpirationPolicy<Integer> policy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        repository = new MemoryDataSource<>(new HashMap<Integer, TestElement>());
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

        verify(policy, atLeastOnce()).notifyDeleted(0);
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