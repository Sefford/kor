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

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.BaseError;
import com.sefford.kor.requests.interfaces.CacheRequest;
import com.sefford.kor.responses.BaseResponse;

/**
 * Cache Request marks the base for a series of uncoupled requests to the Cache
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class CacheExecutionStrategy<R extends BaseResponse, E extends BaseError> extends RequestStrategy<R, E> {
    /**
     * Bus instance to notify the UI the process finished.
     * <p/>
     * This could be also done with Handlers, however the Fragments might exist in an inconsistent
     * state.
     */
    protected final Postable bus;
    /**
     * Logging facilities
     */
    protected final Loggable log;
    /**
     * LogTag
     */
    protected static final String TAG = "CacheRequest";

    /**
     * Creates a new Cache Request
     *
     * @param bus Bus to notify the results
     * @param log Logging facilities
     */
    public CacheExecutionStrategy(Postable bus, Loggable log, CacheRequest<R, E> request) {
        super(bus, log, request);
        this.log = log;
        this.bus = bus;
    }

    @Override
    public void onRun() throws Throwable {
        long start = System.currentTimeMillis();
        final R processedContent = ((CacheRequest<R, E>) request).retrieveFromCache();
        log.d(TAG, request.getRequestName() + "(Retrieving):" + (System.currentTimeMillis() - start) + "ms");
        if (processedContent.isSuccess()) {
            notifySuccess(processedContent);
        }
    }

    @Override
    public void notifyError(E error) {
        // Do nothing
    }
}
