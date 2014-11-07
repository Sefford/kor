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
 * Fast Repository interface is intended for some Repositories to optionally provide an API to
 * save and retrieve elements from themselves in a secondary way.
 * <p/>
 * The objective of this API is to provide a different way of saving and retrieving the elements
 * without accessing the normal get and set methods in which the standard implementation of {@link com.sefford.kor.repositories.BaseRepository BaseRepository}
 * saves and retrieves on several levels sequentially.
 * <p/>
 * This gives the developer the chance to perform a simple save or retrieval in a single level of the
 * repository hierarchy. This is intended to be used with fast-access repositories as memory ones.
 *
 * @author <sefford@gmail.com>
 */
public interface FastRepository<K, V> {

    /**
     * Checks if there is an element in the repository.
     *
     * @param id Id of the element to check
     * @return TRUE if exists, FALSE otherwise
     */
    boolean containsInMemory(K id);

    /**
     * Gets an element from the repository
     *
     * @param id Id of the element to retrieve
     * @return Found element or NULL if it is not stored in the repository
     */
    V getFromMemory(K id);

    /**
     * Returns a list of elements from the repository.
     *
     * @param ids List of IDs to get from Memory.
     * @return A collection of objects related to the inputted IDs. Depending on the implementations,
     * might not be able to fetch all the requested elements from the repository and might not
     * throw any error condition regarding so. This might have to be checked manually.
     */
    Collection<V> getAllFromMemory(List<K> ids);

    /**
     * Saves an element to the repository.
     * <p/>
     * Depending on the implementation, the save method might return an updated version of the element,
     * if it is already in the repository.
     *
     * @param element Element to save.
     */
    V saveInMemory(V element);

    /**
     * Saves a list of elements to the repository.
     * <p/>
     * Depending on the implementation, the saveAllInMemory method might return updated versions of the element,
     * if it is already in the repository.
     *
     * @param elements List of Elements to save.
     */
    Collection<V> saveAllInMemory(Collection<V> elements);
}
