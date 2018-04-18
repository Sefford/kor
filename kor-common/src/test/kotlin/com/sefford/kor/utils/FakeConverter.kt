package com.sefford.kor.utils

import arrow.core.Either
import arrow.core.Right
import com.google.gson.Gson
import com.sefford.kor.repositories.interfaces.JsonConverter
import com.sefford.kor.repositories.utils.RepositoryError

class FakeConverter : JsonConverter<TestElement> {
    val gson = Gson()

    override fun serialize(element: TestElement): Either<RepositoryError, String> = Right(gson.toJson(element))

    override fun deserialize(element: String?): Either<RepositoryError, TestElement> = Right(gson.fromJson(element, TestElement::class.java))
}