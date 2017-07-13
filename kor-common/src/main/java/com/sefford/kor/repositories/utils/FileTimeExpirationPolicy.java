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

import com.sefford.kor.repositories.interfaces.CacheFolder;
import com.sefford.kor.repositories.interfaces.Clock;
import com.sefford.kor.repositories.interfaces.ExpirationPolicy;

import java.io.File;

/**
 * Default time Policy for {@link CacheFolder CacheFolders}. The expiration date is marked by the last modified date
 * of the file.
 *
 * @param <K> Type of the ID
 * @author Saúl Díaz <sefford@gmail.com>
 */
public class FileTimeExpirationPolicy<K> implements ExpirationPolicy<K> {

    /**
     * Default clock implementation based on the system.
     */
    public static final Clock DEFAULT_TIME = new Clock() {
        @Override
        public long getCurrentTime() {
            return System.currentTimeMillis() / Clock.SECOND;
        }

        @Override
        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }
    };
    /**
     * Representation of the underlying file system
     */
    final CacheFolder<K> folder;
    /**
     * Time provider to compare dates
     */
    final Clock time;
    /**
     * Keep alive time of each file.
     */
    final long keepAliveTime;

    /**
     * Default constructor which takes a folder and the keep alive time and uses the default system clock to measure
     * time differences
     *
     * @param folder        Representation of the underlying file system
     * @param keepAliveTime Keep alive time of each file.
     */
    public FileTimeExpirationPolicy(CacheFolder<K> folder, long keepAliveTime) {
        this(folder, DEFAULT_TIME, keepAliveTime);
    }

    /**
     * Default constructor which takes a folder and the keep alive time and uses the a custom Clock system to measure
     * time differences
     *
     * @param folder        Representation of the underlying file system
     * @param time          Time provider to compare dates
     * @param keepAliveTime Keep alive time of each file.
     */
    public FileTimeExpirationPolicy(CacheFolder<K> folder, Clock time, long keepAliveTime) {
        this.folder = folder;
        this.time = time;
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpired(K id) {
        final File file = folder.getFile(id);
        return file == null || time.getCurrentTimeMillis() - file.lastModified() >= keepAliveTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDeleted(K id) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyCreated(K id) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {

    }
}