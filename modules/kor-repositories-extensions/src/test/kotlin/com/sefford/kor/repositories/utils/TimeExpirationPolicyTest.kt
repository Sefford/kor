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
package com.sefford.kor.repositories.utils

import com.sefford.kor.repositories.components.Clock
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class TimeExpirationPolicyTest {

    lateinit var policy: TimeExpirationPolicy<Int>
    @Mock
    internal var clock: Clock? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initMocks(this)

        policy = TimeExpirationPolicy(clock!!, EXPECTED_EXPIRATION_DATE, TimeExpirationPolicy.ON_CREATION_ONLY)
    }

    @Test
    @Throws(Exception::class)
    fun testExpirationWhenTheTimeIsNotSet() {
        assertTrue(policy.isExpired(0))
    }

    @Test
    @Throws(Exception::class)
    fun testExpirationWhenTheTimeIsSetButNotExpired() {
        `when`(clock!!.currentTimeMillis).thenReturn(0L).thenReturn(1L)
        policy.notifyCreated(0)

        assertFalse(policy.isExpired(0))
    }

    @Test
    @Throws(Exception::class)
    fun testExpirationWhenTheTimeIsSetAndExpired() {
        `when`(clock!!.currentTimeMillis).thenReturn(0L).thenReturn(EXPECTED_CUSTOM_TIME)
        policy.notifyCreated(0)

        assertTrue(policy.isExpired(0))
    }

    @Test
    @Throws(Exception::class)
    fun testDeletionNotification() {
        policy.notifyCreated(0)

        policy.notifyDeleted(0)

        assertFalse(policy.times.containsKey(0))
    }

    @Test
    @Throws(Exception::class)
    fun testCreationNotificationWhenCreationOnly() {
        `when`(clock!!.currentTimeMillis).thenReturn(EXPECTED_CUSTOM_TIME)
        policy.notifyCreated(0)

        assertThat(policy.times.get(0), `is`(EXPECTED_CUSTOM_TIME))
    }

    @Test
    @Throws(Exception::class)
    fun testCreationNotificationWhenUpdateEveryTime() {
        policy = TimeExpirationPolicy(clock!!, EXPECTED_EXPIRATION_DATE, TimeExpirationPolicy.REFRESH_EVERY_UPDATE)
        `when`(clock!!.currentTimeMillis).thenReturn(0L)
                .thenReturn(EXPECTED_CUSTOM_TIME)

        policy.notifyCreated(0)
        policy.notifyCreated(0)

        assertThat<Long>(policy.times[0], `is`(EXPECTED_CUSTOM_TIME))
    }

    @Test
    @Throws(Exception::class)
    fun clear() {
        policy.notifyCreated(0)
        policy.notifyCreated(1)

        policy.clear()

        assertFalse(policy.times.containsKey(0))
        assertFalse(policy.times.containsKey(1))
    }

    companion object {

        val EXPECTED_CUSTOM_TIME = 10L
        val EXPECTED_EXPIRATION_DATE = 5L
    }

}
