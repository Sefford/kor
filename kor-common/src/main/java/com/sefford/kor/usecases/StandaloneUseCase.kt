package com.sefford.kor.usecases

import arrow.core.Either
import com.sefford.common.interfaces.Postable
import com.sefford.kor.errors.Error
import com.sefford.kor.responses.Response
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

interface StandaloneUseCase<P : Any, E : Error, R : Response> {

    fun instantiate(params: P): UseCase<E, R>

    fun execute(params: P): Either<E, R> = instantiate(params).execute()

    fun execute(thread: CoroutineContext = CommonPool, postable: Postable, params: P) = launch(thread) { execute(params).fold({ postable.post(it) }, { postable.post(it) }) }

    fun execute(postable: Postable, params: P) = execute(CommonPool, postable, params)

    suspend fun async(params: P): Either<E, R> = kotlinx.coroutines.experimental.async(CommonPool) { instantiate(params).execute() }.await()

}