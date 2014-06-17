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
import com.sefford.kor.requests.interfaces.NetworkRequest;
import com.sefford.kor.responses.BaseResponse;

import retrofit.RetrofitError;

/**
 * Standard saving callback that will execute the request then save to cache before notifying the bus
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class StandardNetworkRequestStrategy<R extends BaseResponse, E extends BaseError> extends NetworkRequestStrategy<R, E> {
    /**
     * Creates a new instance of Saving Callback
     *
     * @param bus     Notification Facility
     * @param log     Logging facilities
     * @param request Request to execute
     */
    public StandardNetworkRequestStrategy(Postable bus, Loggable log, NetworkRequest<R, E> request) {
        super(bus, log, request);
    }

    @Override
    public void onRun() throws Throwable {
        try {
            final R content = ((NetworkRequest<R, E>) request).retrieveNetworkResponse();
            final R processedContent = ((NetworkRequest<R, E>) request).postProcess(content);
            long start = System.currentTimeMillis();
            ((NetworkRequest<R, E>) request).saveToCache(processedContent);
            log.d(TAG, request.getRequestName() + "(Saving):" + (System.currentTimeMillis() - start) + "ms");
            notifySuccess(processedContent);
        } catch (RetrofitError e) {
            log.e(TAG, request.getRequestName(), e);
            notifyError(((NetworkRequest<R, E>) request).composeErrorResponse(e));
        } catch (Exception x) {
            log.e(TAG, request.getRequestName(), x);
            notifyError(((NetworkRequest<R, E>) request).composeErrorResponse(x));
        }
    }
}
