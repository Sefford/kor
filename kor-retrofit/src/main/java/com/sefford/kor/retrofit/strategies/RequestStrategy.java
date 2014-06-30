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
package com.sefford.kor.retrofit.strategies;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.requests.interfaces.RequestIdentification;
import com.sefford.kor.responses.ResponseInterface;
import com.sefford.kor.strategies.interfaces.RequestNotification;

/**
 * Abstract Request Strategy.
 * <p/>
 * It does only provide configuration and success notification logic. It is the base for all Request
 * Strategies.
 * <p/>
 * It delegates to a Request object. In this level, a RequestIdentification implementation for logging purposes,
 * but more specialized versions of this class will require the Request object to implement further
 * interfaces.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class RequestStrategy<R extends ResponseInterface, E extends ErrorInterface> extends Job implements RequestNotification<R, E> {
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
     * Request that will be executed by the callback
     */
    protected final RequestIdentification request;
    /**
     * Logging Facilities
     */
    protected final Loggable log;

    /**
     * Creates a new instance of Request Strategy.
     *
     * @param bus     Notification Facility
     * @param log     Logging facilities
     * @param request Request to execute
     */
    protected RequestStrategy(Postable bus, Loggable log, RequestIdentification request) {
        super(new Params(1000));
        this.bus = bus;
        this.log = log;
        this.request = request;
    }

    @Override
    public void notifySuccess(R content) {
        bus.post(content);
    }

    @Override
    public void onAdded() {
        log.d(TAG, request.getRequestName() + ": On Hold");
    }

    @Override
    protected void onCancel() {
        log.d(TAG, request.getRequestName() + ": Cancelled");
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        log.e(TAG, request.getRequestName(), throwable);
        return false;
    }

    public RequestIdentification getRequest() {
        return request;
    }
}
