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

import com.sefford.kor.repositories.interfaces.CacheFolder
import com.sefford.kor.repositories.interfaces.Clock
import com.sefford.kor.repositories.interfaces.ExpirationPolicy

/**
 * Default time Policy for [CacheFolders][CacheFolder]. The expiration date is marked by the last modified date
 * of the file.
 *
 * @param <K> Type of the ID
 * @author Saúl Díaz <sefford@gmail.com>
</K> */
class FileTimeExpirationPolicy<K>
/**
 * Default constructor which takes a folder and the keep alive time and uses the a custom Clock system to measure
 * time differences
 *
 * @param folder        Representation of the underlying file system
 * @param time          Time provider to compare dates
 * @param keepAliveTime Keep alive time of each file.
 */
(
        /**
         * Representation of the underlying file system
         */
        internal val folder: CacheFolder<K>,
        /**
         * Time provider to compare dates
         */
        internal val time: Clock,
        /**
         * Keep alive time of each file.
         */
        internal val keepAliveTime: Long) : ExpirationPolicy<K> {

    /**
     * Default constructor which takes a folder and the keep alive time and uses the default system clock to measure
     * time differences
     *
     * @param folder        Representation of the underlying file system
     * @param keepAliveTime Keep alive time of each file.
     */
    constructor(folder: CacheFolder<K>, keepAliveTime: Long) : this(folder, DEFAULT_TIME, keepAliveTime) {}

    /**
     * {@inheritDoc}
     */
    override fun isExpired(id: K): Boolean {
        val file = folder.getFile(id)
        return file == null || !file.exists() || time.currentTimeMillis - file.lastModified() >= keepAliveTime
    }

    /**
     * {@inheritDoc}
     */
    override fun notifyDeleted(id: K) {

    }

    /**
     * {@inheritDoc}
     */
    override fun notifyCreated(id: K) {

    }

    /**
     * {@inheritDoc}
     */
    override fun clear() {

    }

    companion object {

        /**
         * Default clock implementation based on the system.
         */
        val DEFAULT_TIME: Clock = object : Clock {
            override val currentTime: Long
                get() = System.currentTimeMillis() / Clock.SECOND

            override val currentTimeMillis: Long
                get() = System.currentTimeMillis()
        }
    }
}