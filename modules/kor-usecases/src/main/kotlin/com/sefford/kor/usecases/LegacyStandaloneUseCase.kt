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
package com.sefford.kor.usecases

import com.sefford.kor.usecases.components.Error
import com.sefford.kor.interactors.delegates.NetworkDelegate
import com.sefford.kor.usecases.components.Response

/**
 * Compatibility Interactor to support old delegate system.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
@Deprecated("Use standalone use case instead")
interface LegacyStandaloneUseCase<P : Any, E : Error, R : Response> : StandaloneUseCase<P, E, R> {

    /**
     * {@inheritDoc}
     */
    override fun instantiate(params: P): UseCase<E, R> {
        val delegate: NetworkDelegate<R, E> = instantiateDelegate(params)
        return UseCase.Execute<E, R> { delegate.execute() }
                .process { delegate.postProcess(it) }
                .persist {
                    delegate.saveToCache(it)
                    it
                }.onError { delegate.composeErrorResponse(it as Exception) }
                .build()
    }

    fun instantiateDelegate(params: P): NetworkDelegate<R, E>

}