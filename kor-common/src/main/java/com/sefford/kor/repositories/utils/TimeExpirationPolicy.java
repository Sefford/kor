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

import com.sefford.kor.repositories.interfaces.ExpirationPolicy;
import com.sefford.kor.repositories.interfaces.Clock;

import java.util.HashMap;

/**
 * Expiration policy based on save time.
 * <p>
 * This policy evicts elements based on the time difference between the saved and the query of the element. Can be
 * configured to be allowed to refresh the time every time the element is saved or not.
 *
 * @author Saul Diaz Gonzalez <sefford@gmail.com>
 */
public class TimeExpirationPolicy<K> implements ExpirationPolicy<K> {

    /**
     * Convenience semantic constant to use the time expiration to set the time once
     */
    public static final boolean ON_CREATION_ONLY = true;
    /**
     * Convenience semantic constant to let the time expiration be refreshed on every save
     */
    public static final boolean REFRESH_EVERY_UPDATE = false;

    /**
     * Default time implementation based on the system time.
     */
    private static final Clock DEFAULT_TIME_IMPLEMENTATION = new Clock() {
        @Override
        public long getCurrentTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    /**
     * Time policy
     */
    final Clock clock;
    /**
     * Time to keep alive an element before marking it as "expired"
     */
    final long keepAliveTime;
    /**
     * Flag that indicates the policy to refresh the time every time it is saved.
     * <p>
     * If FALSE, the element will update the time every time it is saved, otherwise it will be saved on initialization.
     */
    final boolean absolute;
    /**
     *
     */
    final HashMap<K, Long> times;

    /**
     * Creates a new TimePolicy with default time implementation.
     *
     * @param keepAliveTime Time to keep elements alive
     * @param absolute      flag to indicate if the elements last update time need to be refreshed or not
     */
    public TimeExpirationPolicy(long keepAliveTime, boolean absolute) {
        this(DEFAULT_TIME_IMPLEMENTATION, keepAliveTime, absolute);
    }

    /**
     * Creates a new TimePolicy with custom time implementation.
     *
     * @param clock         Time policy
     * @param keepAliveTime Time to keep elements alive
     * @param absolute      flag to indicate if the elements last update time need to be refreshed or not
     */
    public TimeExpirationPolicy(Clock clock, long keepAliveTime, boolean absolute) {
        this.clock = clock;
        this.keepAliveTime = keepAliveTime;
        this.absolute = absolute;
        this.times = new HashMap<>(0, .75f);
    }

    @Override
    public boolean isExpired(K id) {
        final Long time = times.get(id);
        return time == null || (clock.getCurrentTimeMillis() - time.longValue() >= keepAliveTime);
    }

    @Override
    public void notifyDeleted(K id) {
        times.remove(id);
    }

    @Override
    public void notifyCreated(K id) {
        if (!times.containsKey(id) || !absolute) {
            times.put(id, clock.getCurrentTimeMillis());
        }
    }

    @Override
    public void clear() {
        times.clear();
    }
}
