package com.sefford.kor.usecases

import arrow.core.Either
import arrow.core.flatMap
import com.sefford.kor.errors.Error
import com.sefford.kor.repositories.utils.emptyErrorHandler
import com.sefford.kor.repositories.utils.noop
import com.sefford.kor.responses.Response
import kotlinx.coroutines.experimental.async

class UseCase<R : Response, E : Error> private constructor(internal val logic: () -> Either<Exception, R>,
                                                           internal val postProcessor: (response: R) -> Either<Exception, R> = noop(),
                                                           internal val cachePersistance: (response: R) -> Either<Exception, R> = noop(),
                                                           internal val errorHandler: (ex: Exception) -> E) {

    fun <E : Error, R : Response> execute(): Either<E, R> {
        return logic()
                .flatMap { postProcessor(it) }
                .flatMap { cachePersistance(it) }
                .fold({ Either.left(errorHandler(it)) as Either<E, R> },
                        { Either.right(it) as Either<E, R> })
    }

    suspend fun <E : Error, R : Response> aysnc(): Either<E, R> {
        return async { execute<E, R>() }.await()
    }

    class Execute<R : Response, E : Error>(internal val logic: () -> Either<Exception, R>) {
        internal var postProcessor: (response: R) -> Either<Exception, R> = noop()
        internal var cachePersistance: (response: R) -> Either<Exception, R> = noop()
        internal var errorHandler: (ex: Exception) -> E = emptyErrorHandler()

        fun process(postProcessor: (response: R) -> Either<Exception, R>): Execute<R, E> {
            this.postProcessor = postProcessor
            return this
        }

        fun persist(cachePersistance: (response: R) -> Either<Exception, R>): Execute<R, E> {
            this.cachePersistance = cachePersistance
            return this
        }

        fun onError(errorHandler: (ex: Exception) -> E): Execute<R, E> {
            this.errorHandler = errorHandler
            return this;
        }

        fun build(): UseCase<R, E> {
            return UseCase(logic, postProcessor, cachePersistance, errorHandler)
        }
    }
}