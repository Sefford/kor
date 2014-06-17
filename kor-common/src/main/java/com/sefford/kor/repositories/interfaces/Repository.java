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
package com.sefford.kor.repositories.interfaces;

import java.util.Collection;
import java.util.List;

/**
 * Interface for Repositories. They work on a simple CRUD schema.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface Repository<K, V extends RepoElement<K>> {

    /**
     * Clears the repository
     */
    void clear();

    /**
     * Checks if there is an element in the repository
     *
     * @param id Id of the element to check
     * @return TRUE if exists, FALSE otherwise
     */
    boolean contains(K id);

    /**
     * Deletes an element from the repository
     *
     * @param id
     * @param element
     */
    void delete(K id, V element);

    /**
     * Deletes a list of elements from the repository
     *
     * @param elements List of elements to delete
     */
    void deleteAll(List<V> elements);

    /**
     * Gets an element from the repository
     *
     * @param id Id of the element to retrieve
     * @return Found element or NULL if it is not stored in the repository
     */
    V get(K id);

    /**
     * Returns all elements in repository
     */
    Collection<V> getAll();

    /**
     * Saves an element to the repository
     *
     * @param element Element to save
     */
    V save(V element);

    /**
     * Saves a list of elements to the repository
     *
     * @param elements List of Elements to save
     */
    Collection<V> saveAll(Collection<V> elements);

    /**
     * Is available will return if the Repository is available (i.e. it is initialized)
     *
     * @return TRUE if the Repository is operative, FALSE otherwise
     */
    boolean isAvailable();
}
