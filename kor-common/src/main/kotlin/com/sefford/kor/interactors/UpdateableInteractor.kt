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
import com.sefford.kor.interactors.interfaces.UpdateableDelegate
import com.sefford.kor.responses.Response

/**
 * Updateable Interactor, intended for networking. Keeps executing a phase of polling until it decides
 * it has to stop.
 *
 *
 * Each of the loops will terminate in a call to [notify][Notifiable.notify]
 * if no unexpected condition happened.
 *
 *
 * Otherwise, it will produce a [Notifiable.notify]  notify} response.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class UpdateableInteractor<R : Response, E : Error>
/**
 * Creates a new instance of Updateable Interactor
 *
 * @param bus      Notification Facility
 * @param log      Logging facilities
 * @param delegate Request to execute
 */
(bus: Postable, log: Loggable, delegate: UpdateableDelegate<R, E>) : NetworkInteractor<R, E>(bus, log, delegate) {

    override fun run() {
        val start = System.currentTimeMillis()
        try {
            delegate.startPerformanceLog(log)
            while ((delegate as UpdateableDelegate<*, *>).keepLooping()) {
                val content = (delegate as NetworkDelegate<R, E>).execute()
                val processedContent = (delegate as NetworkDelegate<R, E>).postProcess(content)
                (delegate as NetworkDelegate<R, E>).saveToCache(processedContent)
                notify(processedContent)
            }
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
        } catch (x: Exception) {
            val error = (delegate as UpdateableDelegate<R, E>).composeErrorResponse(x)
            if (loggable && error.loggable) {
                delegate.logErrorResponse(log, x)
            }
            notify(error)
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
        }

    }

    override fun execute(): Any {
        throw IllegalThreadStateException("Cannot be executed syncronously")
    }
}