package com.sefford.kor.usecases

import arrow.core.Either
import arrow.core.Try
import arrow.core.identity
import com.sefford.kor.errors.Error
import com.sefford.kor.repositories.utils.emptyErrorHandler
import com.sefford.kor.responses.Response

class UseCase<E : Error, R : Response> private constructor(internal val logic: () -> R,
                                                           internal val postProcessor: (response: R) -> R = ::identity,
                                                           internal val cachePersistance: (response: R) -> R = ::identity,
                                                           internal val errorHandler: (ex: Throwable) -> E) {

    fun execute(): Either<E, R> {
        return Try { cachePersistance(postProcessor(logic())) }
                .fold({ Either.left(errorHandler(it)) },
                        { Either.right(it) })
    }

    class Execute<E : Error, R : Response>(internal val logic: () -> R) {
        internal var postProcessor: (R) -> R = ::identity
        internal var cachePersistance: (R) -> R = ::identity
        internal var errorHandler: (Throwable) -> E = emptyErrorHandler()

        fun process(postProcessor: (R) -> R): Execute<E, R> {
            this.postProcessor = postProcessor
            return this
        }

        fun persist(cachePersistance: (R) -> R): Execute<E, R> {
            this.cachePersistance = cachePersistance
            return this
        }

        fun onError(errorHandler: (Throwable) -> E): Execute<E, R> {
            this.errorHandler = errorHandler
            return this;
        }

        fun build(): UseCase<E, R> {
            return UseCase(logic, postProcessor, cachePersistance, errorHandler)
        }
    }
}