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
package com.sefford.kor.interactors;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.Delegate;
import com.sefford.kor.interactors.interfaces.InteractorNotification;
import com.sefford.kor.responses.Response;

/**
 * Abstract Interactor
 * <p/>
 * It does only provide configuration and success notification logic. It is the base for all Interactors.
 * .
 * <p/>
 * It delegates its work to a "Delegate" object. In this level, only a InteractorNotificacion implementation exists
 * for logging purposes, but more specialized versions of this class will require the delegate object
 * to implement further interfaces to properly follow each of the strategies.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class Interactor<R extends Response, E extends Error> implements Runnable, InteractorNotification<R, E> {
    /**
     * Network Tag for Logging
     */
    public static final String TAG = "KorNetwork";
    /**
     * Postable interface to notify the UI the process finished.
     * <p/>
     * This could be also done with Handlers, however the Fragments might exist in an inconsistent
     * state.
     */
    protected final Postable bus;
    /**
     * Delegate that will be executed by the callback
     */
    protected final Delegate delegate;
    /**
     * Logging Facilities
     */
    protected final Loggable log;

    /**
     * Creates a new instance of a Base Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    protected Interactor(Postable bus, Loggable log, Delegate delegate) {
        this.bus = bus;
        this.log = log;
        this.delegate = delegate;
    }

    @Override
    public void notifySuccess(R content) {
        bus.post(content);
    }

    /**
     * Returns the target delegate for this Interactor
     *
     * @return Executing delegate
     */
    public Delegate getDelegate() {
        return delegate;
    }
}
