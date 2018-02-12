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

import com.sefford.kor.repositories.interfaces.Clock
import com.sefford.kor.repositories.interfaces.ExpirationPolicy

import java.util.HashMap

/**
 * Expiration policy based on save time.
 *
 *
 * This policy evicts elements based on the time difference between the saved and the query of the element. Can be
 * configured to be allowed to refresh the time every time the element is saved or not.
 *
 * @author Saul Diaz Gonzalez <sefford></sefford>@gmail.com>
 */
class TimeExpirationPolicy<K>
/**
 * Creates a new TimePolicy with custom time implementation.
 *
 * @param clock         Time policy
 * @param keepAliveTime Time to keep elements alive
 * @param absolute      flag to indicate if the elements last update time need to be refreshed or not
 */
(
        /**
         * Time policy
         */
        internal val clock: Clock,
        /**
         * Time to keep alive an element before marking it as "expired"
         */
        internal val keepAliveTime: Long,
        /**
         * Flag that indicates the policy to refresh the time every time it is saved.
         *
         *
         * If FALSE, the element will update the time every time it is saved, otherwise it will be saved on initialization.
         */
        internal val absolute: Boolean) : ExpirationPolicy<K> {
    /**
     *
     */
    internal val times: HashMap<K, Long>

    /**
     * Creates a new TimePolicy with default time implementation.
     *
     * @param keepAliveTime Time to keep elements alive
     * @param absolute      flag to indicate if the elements last update time need to be refreshed or not
     */
    constructor(keepAliveTime: Long, absolute: Boolean) : this(DEFAULT_TIME_IMPLEMENTATION, keepAliveTime, absolute) {}

    init {
        this.times = HashMap(0, .75f)
    }

    override fun isExpired(id: K): Boolean {
        val time = times[id]
        return time == null || clock.currentTimeMillis - time.toLong() >= keepAliveTime
    }

    override fun notifyDeleted(id: K) {
        times.remove(id)
    }

    override fun notifyCreated(id: K) {
        if (!times.containsKey(id) || !absolute) {
            times[id] = clock.currentTimeMillis
        }
    }

    override fun clear() {
        times.clear()
    }

    companion object {

        /**
         * Convenience semantic constant to use the time expiration to set the time once
         */
        val ON_CREATION_ONLY = true
        /**
         * Convenience semantic constant to let the time expiration be refreshed on every save
         */
        val REFRESH_EVERY_UPDATE = false

        /**
         * Default time implementation based on the system time.
         */
        private val DEFAULT_TIME_IMPLEMENTATION = object : Clock {
            override val currentTime: Long
                get() = System.currentTimeMillis()

            override val currentTimeMillis: Long
                get() = System.currentTimeMillis()
        }
    }
}
