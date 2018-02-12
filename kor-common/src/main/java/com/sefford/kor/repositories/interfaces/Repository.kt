/*
 * Copyright (C) 2014 Saúl Díaz
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

import com.sefford.kor.repositories.TwoTierRepository

/**
 * Interface for Repositories.
 *
 *
 * They work on a simple CRUD API.
 *
 *
 * Aditionally each repository can flag in advance if the repository itself is "ready" through [isAvailable interface][Repository.isAvailable].
 * Might be cases, as Network Repositories or Disk Repositories where the availability might not be guaranteed
 * because of hardware or connectivity problems.
 *
 *
 * Checking in advance for the availability of the repository might save the developer unnecessary errors
 * because the repository is in an incorrect state.
 *
 *
 * The availability of a repo may vary over time and everytime an operation is performed over the repository
 * the availability will be checked.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface Repository<K, V> {

    /**
     * Returns all elements from the repository
     */
    val all: Collection<V>

    /**
     * Is available will return if the Repository is available (i.e. it is initialized)-
     *
     *
     * As this method is called every time an operation is performed on the repository if extending from
     * [TwoTierRepository] to discern if the next level
     * of the repository is available, it is recommended this to be a easy and fast operation.
     *
     * @return TRUE if the Repository is operative, FALSE otherwise
     */
    val isAvailable: Boolean

    /**
     * Clears the repository
     */
    fun clear()

    /**
     * Checks if there is an element in the repository
     *
     * @param id Id of the element to check
     * @return TRUE if exists, FALSE otherwise
     */
    operator fun contains(id: K): Boolean

    /**
     * Deletes an element from the repository
     *
     * @param id id to delete
     * @param element element to delete
     */
    fun delete(id: K, element: V?)

    /**
     * Deletes an element from the repository
     *
     * @param id id to delete
     */
    fun delete(id: K)


    /**
     * Deletes a list of elements from the repository
     *
     * @param elements List of elements to delete
     */
    fun deleteAll(elements: Collection<V>)

    /**
     * Gets an element from the repository
     *
     * @param id Id of the element to retrieve
     * @return Found element or NULL if it is not stored in the repository
     */
    operator fun get(id: K): V?

    /**
     * Returns all requested elements
     */
    fun getAll(ids: Collection<K>): Collection<V>

    /**
     * Saves an element to the repository
     *
     * @param element Element to save
     */
    fun save(element: V): V

    /**
     * Saves a list of elements to the repository
     *
     * @param elements List of Elements to save
     */
    fun saveAll(elements: Collection<V>): Collection<V>
}
