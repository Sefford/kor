package com.sefford.kor.repositories.converters

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.sefford.kor.repositories.components.JsonConverter
import com.sefford.kor.repositories.components.RepositoryError
import com.squareup.moshi.Moshi

class MoshiConverter<V>(val moshi: Moshi, val clazz: Class<V>) : JsonConverter<V> {

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