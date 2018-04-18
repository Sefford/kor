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

/**
 * Interface for updating an element.
 *
 *
 * This could be extended to return a T' type so the interface would be more flexible and allow other types updating between them.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface Updateable<T> {

    /**
     * Updates an element with the data of other of its kind.
     *
     * @param other Element to update with
     * @return The original element updated with the data of other
     */
    fun update(other: T): T
}
