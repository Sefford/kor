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
package com.sefford.kor.repositories.interfaces

/**
 * Interface to abstract time fetching for the [TimeExpirationPolicy][com.sefford.kor.repositories.utils.TimeExpirationPolicy]
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface Clock {

    /**
     * Gets the current time in seconds
     *
     * @return Current time in seconds
     */
    val currentTime: Long

    /**
     * Gets the current time in milliseconds
     *
     * @return Current time in millisecond
     */
    val currentTimeMillis: Long

    companion object {
        /**
         * Milliseconds that make a Second
         */
        val SECOND: Long = 1000
        /**
         * Milliseconds that make a Minute
         */
        val MINUTE = 60 * SECOND
        /**
         * Milliseconds that make an Hour
         */
        val HOUR = 60 * MINUTE
        /**
         * Milliseconds that make a Day
         */
        val DAY = 24 * HOUR
        /**
         * Milliseconds that make a Week
         */
        val WEEK = 7 * DAY

        /**
         * Milliseconds that make a Year
         */
        val YEAR = 365 * DAY
    }
}
