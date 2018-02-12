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
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Saúl Díaz <sefford@gmail.com>
 */
public class FileTimeExpirationPolicyTest {
    static final String EXPECTED_ID = "any_id";

    FileTimeExpirationPolicy<String> fileTimeExpirationPolicy;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldMarkAsExpiredWhenFileDoesNotExist() throws Exception {
        fileTimeExpirationPolicy = new FileTimeExpirationPolicy<>(new CacheFolder<String>() {
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
                return  mock(File.class);
            }
        }, 0);

        assertTrue(fileTimeExpirationPolicy.isExpired(EXPECTED_ID));
    }

    @Test
    public void shouldMarkAsExpiredWhenFileIsExpired() throws Exception {
        fileTimeExpirationPolicy = new FileTimeExpirationPolicy<>(new CacheFolder<String>() {
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
                if (EXPECTED_ID.equals(id)) {
                    File mock = mock(File.class);
                    when(mock.lastModified()).thenReturn(0L);
                    return mock;
                }
                return null;
            }
        }, new Clock() {
            @Override
            public long getCurrentTime() {
                return 10000L;
            }

            @Override
            public long getCurrentTimeMillis() {
                return 10000L;
            }
        }, 1);

        assertTrue(fileTimeExpirationPolicy.isExpired(EXPECTED_ID));
    }

    @Test
    public void shouldMarkAsExpiredWhenFileIsNotExpired() throws Exception {
        fileTimeExpirationPolicy = new FileTimeExpirationPolicy<>(new CacheFolder<String>() {
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
                if (EXPECTED_ID.equals(id)) {
                    File mock = mock(File.class);
                    when(mock.lastModified()).thenReturn(100L);
                    when(mock.exists()).thenReturn(true);
                    return mock;
                }
                return null;
            }
        }, new Clock() {
            @Override
            public long getCurrentTime() {
                return 101L;
            }

            @Override
            public long getCurrentTimeMillis() {
                return 101L;
            }
        }, 100L);

        assertFalse(fileTimeExpirationPolicy.isExpired(EXPECTED_ID));
    }

}