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
import com.sefford.kor.repositories.interfaces.Populator

/**
 * Populator strategy for physical [CacheFolders][CacheFolder]
 *
 * @param <K> Type of the IDs that maps this populator
 * @author Saúl Díaz <sefford@gmail.com>
</K> */
abstract class FilePopulator<K>
/**
 * Creates a new instance of FilePopulator pointing to folder
 *
 * @param folder Folder that represents a directory in the file System
 */
(
        /**
         * CacheFolder which represents the underlying File system
         */
        val folder: CacheFolder<K>) : Populator<K> {

    /**
     * {@inheritDoc}
     */
    override fun populate(lru: LruCache<K>) {
        if (!folder.exists()) {
            return
        }
        val files = folder.files()
        if (files.size == 0) {
            return
        }
        for (i in files.indices) {
            lru.put(convert(files[i].name.replace(".json".toRegex(), "")))
        }
    }
}
