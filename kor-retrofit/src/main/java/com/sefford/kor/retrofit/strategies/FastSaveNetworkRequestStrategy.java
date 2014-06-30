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
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.requests.interfaces.FastSaving;
import com.sefford.kor.requests.interfaces.NetworkRequest;
import com.sefford.kor.responses.ResponseInterface;

import retrofit.RetrofitError;

/**
 * Specialization of a Network Request Strategy to support fast cache saving.
 * <p/>
 * This strategy adds a first fast save on the repository to have the information already on the repository,
 * then notifies the UI. After the notification a standard full-save is performed on the repository
 * in the background.
 * <p/>
 * An error condition will only be thrown if there is any issue before the notification. After it, it
 * will only be logged to avoid error duplication.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class FastSaveNetworkRequestStrategy<R extends ResponseInterface, E extends ErrorInterface> extends NetworkRequestStrategy<R, E> {

    /**
     * Creates a new instance of Saving Callback
     *
     * @param bus     Notification Facility
     * @param log     Logging facilities
     * @param request Request to execute
     */
    public FastSaveNetworkRequestStrategy(Postable bus, Loggable log, NetworkRequest<R, E> request) {
        super(bus, log, request);
    }

    @Override
    public void onRun() throws Throwable {
        try {
            final R content = ((NetworkRequest<R, E>) request).retrieveNetworkResponse();
            final R processedContent = ((NetworkRequest<R, E>) request).postProcess(content);
            final R savedMemoryContent = ((FastSaving<R>) request).fastSave(processedContent);
            notifySuccess(savedMemoryContent);
            try {
                long start = System.currentTimeMillis();
                ((NetworkRequest<R, E>) request).saveToCache(savedMemoryContent);
                log.printPerformanceLog(TAG, request.getRequestName(), start);
            } catch (Exception x) {
                // On the inner exception we just do not notify the error
                log.e(TAG, request.getRequestName(), x);
            }
        } catch (RetrofitError e) {
            log.e(TAG, request.getRequestName(), e);
            notifyError(((NetworkRequest<R, E>) request).composeErrorResponse(e));
        } catch (Exception x) {
            log.e(TAG, request.getRequestName(), x);
            notifyError(((NetworkRequest<R, E>) request).composeErrorResponse(x));
        }
    }
}
