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

import com.sefford.kor.repositories.utils.LruCache

/**
 * Abstraction of a tool to optionally populate [LRU Repositories][com.sefford.kor.repositories.LruRepository]
 *
 * @param <K> Type of the IDs of the Repository.
 * @author Saúl Díaz <sefford@gmail.com>
</K> */
interface Populator<K> {

    /**
     * Populates the given LRU following a strategy defined by the implementation.
     *
     * @param lru LRU of the LRURepository
     */
    fun populate(lru: LruCache<K>)

    /**
     * Utility method to convert from the name of the file to the ID that will be added to the LRU.
     *
     * @param name Name of the file
     * @return ID in the type of the LRU
     */
    fun convert(name: String): K
}
