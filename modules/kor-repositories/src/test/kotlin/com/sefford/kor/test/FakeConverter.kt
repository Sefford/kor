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
package com.sefford.kor.test

import arrow.core.Either
import arrow.core.Right
import com.google.gson.Gson
import com.sefford.kor.repositories.components.JsonConverter
import com.sefford.kor.repositories.components.RepositoryError

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class FakeConverter : JsonConverter<TestElement> {
    val gson = Gson()

    override fun serialize(element: TestElement): Either<RepositoryError, String> = Right(gson.toJson(element))

    override fun deserialize(element: String?): Either<RepositoryError, TestElement> = Right(gson.fromJson(element, TestElement::class.java))
}