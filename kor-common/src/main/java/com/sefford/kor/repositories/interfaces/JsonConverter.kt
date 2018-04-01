package com.sefford.kor.repositories.interfaces

import arrow.core.Either
import com.sefford.kor.interactors.RepositoryError

interface JsonConverter<V> {

    fun serialize(element: V): Either<RepositoryError, String>

    fun deserialize(element: String?): Either<RepositoryError, V>

}