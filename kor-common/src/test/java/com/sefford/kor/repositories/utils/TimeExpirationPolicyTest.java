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
package com.sefford.kor.repositories.utils;

import com.sefford.kor.repositories.interfaces.Clock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class TimeExpirationPolicyTest {

    public static final long EXPECTED_CUSTOM_TIME = 10l;
    public static final long EXPECTED_EXPIRATION_DATE = 5l;
    TimeExpirationPolicy<Integer> policy;
    @Mock
    Clock clock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        policy = new TimeExpirationPolicy<>(clock, EXPECTED_EXPIRATION_DATE, TimeExpirationPolicy.ON_CREATION_ONLY);
    }

    @Test
    public void testExpirationWhenTheTimeIsNotSet() throws Exception {
        assertTrue(policy.isExpired(0));
    }

    @Test
    public void testExpirationWhenTheTimeIsSetButNotExpired() throws Exception {
        when(clock.getCurrentTimeMillis()).thenReturn(0l).thenReturn(1l);
        policy.notifyCreated(0);

        assertFalse(policy.isExpired(0));
    }

    @Test
    public void testExpirationWhenTheTimeIsSetAndExpired() throws Exception {
        when(clock.getCurrentTimeMillis()).thenReturn(0l).thenReturn(EXPECTED_CUSTOM_TIME);
        policy.notifyCreated(0);

        assertTrue(policy.isExpired(0));
    }

    @Test
    public void testDeletionNotification() throws Exception {
        policy.notifyCreated(0);

        policy.notifyDeleted(0);

        assertFalse(policy.times.containsKey(0));
    }

    @Test
    public void testCreationNotificationWhenCreationOnly() throws Exception {
        when(clock.getCurrentTimeMillis()).thenReturn(EXPECTED_CUSTOM_TIME);
        policy.notifyCreated(0);

        assertThat(policy.times.get(0), is(EXPECTED_CUSTOM_TIME));
    }

    @Test
    public void testCreationNotificationWhenUpdateEveryTime() throws Exception {
        policy = new TimeExpirationPolicy<Integer>(clock, EXPECTED_EXPIRATION_DATE, TimeExpirationPolicy.REFRESH_EVERY_UPDATE);
        when(clock.getCurrentTimeMillis()).thenReturn(0l)
                .thenReturn(EXPECTED_CUSTOM_TIME);

        policy.notifyCreated(0);
        policy.notifyCreated(0);

        assertThat(policy.times.get(0), is(EXPECTED_CUSTOM_TIME));
    }

    @Test
    public void clear() throws Exception {
        policy.notifyCreated(0);
        policy.notifyCreated(1);

        policy.clear();

        assertFalse(policy.times.containsKey(0));
        assertFalse(policy.times.containsKey(1));
    }

}