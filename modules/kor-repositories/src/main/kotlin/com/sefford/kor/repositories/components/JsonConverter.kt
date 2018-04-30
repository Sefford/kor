/*
 * Copyright (C) 2018 Saúl Díaz
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
package com.sefford.kor.repositories.components

import arrow.core.Either

/**
 * Abstracts the Json conversion from a Json-based datasource
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface JsonConverter<V> {

    /**
     * Element-to-String element serialization
     *
     * @param element Element to serialize
     */
    fun serialize(element: V): Either<RepositoryError, String>

    /**
     * Json-to-element serialization
     *
     * @param element Element to deserialize. Note that some not Kotlin frameworks may serialize to null
     */
    fun deserialize(element: String?): Either<RepositoryError, V>

}
