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
import com.sefford.kor.interactors.delegates.Delegate
import com.sefford.kor.interactors.delegates.Notifiable
import com.sefford.kor.usecases.components.Response
import com.sefford.kor.usecases.components.Error
/**
 * Abstract Interactor
 *
 *
 * It does only provide configuration and success notification logic. It is the base for all Interactors.
 * .
 *
 *
 * It delegates its work to a "Delegate" object. In this level, only a InteractorNotificacion implementation exists
 * for logging purposes, but more specialized versions of this class will require the delegate object
 * to implement further interfaces to properly follow each of the strategies.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
abstract class Interactor<R : Response, E : Error>
/**
 * Creates a new instance of a Base Interactor
 *
 * @param postable Notification Facility
 * @param log      Logging facilities
 * @param delegate Request to execute
 * @param loggable Logging flag
 */
@JvmOverloads protected constructor(
        /**
         * Postable interface to notify the UI the process finished.
         *
         *
         * This could be also done with Handlers, however the Fragments might exist in an inconsistent
         * state.
         */
        protected val postable: Postable,
        /**
         * Logging Facilities
         */
        protected val log: Loggable,
        /**
         * Delegate that will be executed by the callback
         */
        /**
         * Returns the target delegate for this Interactor
         *
         * @return Executing delegate
         */
        val delegate: Delegate<*, *>,
        /**
         * Enables or disables all logging on the interactor
         */
        protected var loggable: Boolean = false) : Runnable, Notifiable {

    override fun run() {
        notify(execute())
    }

    override fun notify(content: Any) {
        postable.post(content)
    }

    abstract fun execute(): Any

    companion object {
        /**
         * Network Tag for Logging
         */
        val TAG = "KorNetwork"
    }
}
/**
 * Creates a new instance of a Base Interactor
 *
 * @param postable Notification Facility
 * @param log      Logging facilities
 * @param delegate Request to execute
 */
