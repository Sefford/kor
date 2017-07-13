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
package com.sefford.kor.repositories.interfaces;

import java.io.File;

/**
 * Abstraction of a Cache Folder
 *
 * @param <K> Type of IDs that will storaged in the CacheFolder
 * @author Saúl Díaz <sefford@gmail.com>
 */
public interface CacheFolder<K> {

    /**
     * Retrieves all the files in this cache Folder
     *
     * @return All files in the folder
     */
    File[] files();

    /**
     * Checks if a file exists in the folder
     *
     * @return TRUE if exists, FALSE otherwise
     */
    boolean exists();

    /**
     * Given an ID in the <K> type, returns its related file.
     * <p>
     * This method works basically as a mapper between the underlying folder and the File itself.
     *
     * @param id ID of the element to get the file from
     * @return A valid file to read the element from
     */
    File getFile(K id);

}