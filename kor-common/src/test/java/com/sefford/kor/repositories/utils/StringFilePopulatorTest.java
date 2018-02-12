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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class StringFilePopulatorTest {

    public static final int EXPECTED_NUMBER_OF_FILES = 10;
    StringFilePopulator populator;
    LruCache<String> lruCache;

    @Before
    public void setUp() throws Exception {
        lruCache = new LruCache<>(200);
    }

    @Test
    public void shouldPopulateCorrectlyGivenFiles() throws Exception {
        populator = new StringFilePopulator(new FakeCacheFolder());

        populator.populate(lruCache);

        for (int i = 0; i < EXPECTED_NUMBER_OF_FILES; i++) {
            assertTrue(lruCache.contains(Integer.toString(i)));
        }
    }

    @Test
    public void shouldNotPopulateIfCacheDoesNotExist() throws Exception {
        populator = new StringFilePopulator(new CacheFolder<String>() {
            @Override
            public File[] files() {
                return new File[0];
            }

            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public File getFile(String id) {
                return null;
            }
        });

        populator.populate(lruCache);

        for (int i = 0; i < EXPECTED_NUMBER_OF_FILES; i++) {
            assertFalse(lruCache.contains(Integer.toString(i)));
        }
    }

    @Test
    public void shouldNotPopulateIfFolderIsEmpty() throws Exception {
        populator = new StringFilePopulator(new CacheFolder<String>() {
            @Override
            public File[] files() {
                return new File[0];
            }

            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public File getFile(String id) {
                return null;
            }
        });

        populator.populate(lruCache);

        for (int i = 0; i < EXPECTED_NUMBER_OF_FILES; i++) {
            assertFalse(lruCache.contains(Integer.toString(i)));
        }
    }

    class FakeCacheFolder implements CacheFolder<String> {

        @Override
        public File[] files() {
            final List<File> files = new ArrayList<>();
            for (int i = 0; i < EXPECTED_NUMBER_OF_FILES; i++) {
                final File mockedFile = mock(File.class);
                when(mockedFile.getName()).thenReturn(Integer.toString(i) + ".json");
                files.add(mockedFile);
            }
            final File[] result = new File[files.size()];
            return files.toArray(result);
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public File getFile(String id) {
            return null;
        }
    }
}