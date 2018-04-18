/*
 * Copyright (C) 2014 Saúl Díaz
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
package com.sefford.kor.interactors


import com.sefford.common.interfaces.Loggable
import com.sefford.common.interfaces.Postable
import com.sefford.kor.errors.Error
import com.sefford.kor.interactors.interfaces.NetworkDelegate
import com.sefford.kor.interactors.interfaces.Notifiable
import com.sefford.kor.responses.Response

/**
 * Standard Network Interactor that performs the Request Process in this order:
 * *
 *  * Network Retrieval phase.
 *  * Post processing.
 *  * Saving to lru.
 *  * Notifying to the UI for success
 *
 *
 *
 *
 *
 * In any moment the Strategy can notify of an error through [NotifyError][Notifiable.notify] interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class StandardNetworkInteractor<R : Response, E : Error>
/**
 * Creates a new instance of Standard Interactor
 *
 * @param bus      Notification Facility
 * @param log      Logging facilities
 * @param delegate Request to execute
 */
(bus: Postable, log: Loggable, delegate: NetworkDelegate<R, E>) : NetworkInteractor<R, E>(bus, log, delegate) {

    override fun execute(): Any {
        val start = System.currentTimeMillis()
        try {
            delegate.startPerformanceLog(log)
            val content = (delegate as NetworkDelegate<R, E>).execute()
            val processedContent = delegate.postProcess(content)
            delegate.saveToCache(processedContent)
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return processedContent
        } catch (x: Exception) {
            val error = (delegate as NetworkDelegate<R, E>).composeErrorResponse(x)
            if (loggable && error.loggable) {
                delegate.logErrorResponse(log, x)
            }
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return error
        }
    }
}
