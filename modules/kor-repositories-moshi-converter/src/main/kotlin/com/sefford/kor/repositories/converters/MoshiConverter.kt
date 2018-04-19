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
package com.sefford.kor.repositories.converters

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.sefford.kor.repositories.components.JsonConverter
import com.sefford.kor.repositories.components.RepositoryError
import com.squareup.moshi.Moshi

/**
 * Implementation of JsonConverter to make use of Moshi
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class MoshiConverter<V>
/**
 * Constructor of MoshiConverter
 *
 * @param moshi instance
 * @param clazz element type this converter will provide
 */
constructor(private val moshi: Moshi,
            private val clazz: Class<V>) : JsonConverter<V> {

    override fun deserialize(element: String?): Either<RepositoryError, V> {
        return try {
            Right(moshi.adapter<V>(clazz).fromJson(element!!)!!)
        } catch (x: Exception) {
            Left(RepositoryError.CannotRetrieve(x))
        }
    }

    override fun serialize(element: V): Either<RepositoryError, String> {
        return try {
            Right(moshi.adapter<V>(clazz).toJson(element))
        } catch (x: Exception) {
            Left(RepositoryError.CannotPersist(x))
        }
    }
}