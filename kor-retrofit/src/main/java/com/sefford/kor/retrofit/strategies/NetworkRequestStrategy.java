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
import com.sefford.kor.requests.interfaces.NetworkRequest;
import com.sefford.kor.responses.ResponseInterface;

/**
 * Base Network Request Strategy.
 * <p/>
 * This abstract class starts notifying the errors to the UI in preparation for Network Requests.
 * <p/>
 * This hierarchy of classes will delegate the work to NetworkRequests implementations.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class NetworkRequestStrategy<R extends ResponseInterface, E extends ErrorInterface> extends RequestStrategy<R, E> {
    /**
     * Creates a new instance of Base Network Strategy.
     *
     * @param bus     Notification Facility
     * @param log     Logging facilities
     * @param request Request to execute
     */
    protected NetworkRequestStrategy(Postable bus, Loggable log, NetworkRequest<R, E> request) {
        super(bus, log, request);
    }

    @Override
    public void notifyError(E error) {
        bus.post(error);
    }

}
