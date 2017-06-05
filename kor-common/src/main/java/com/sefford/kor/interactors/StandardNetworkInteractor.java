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


import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.interactors.interfaces.Notifiable;
import com.sefford.kor.responses.Response;

/**
 * Standard Network Interactor that performs the Request Process in this order:
 * * <ul>
 * <li>Network Retrieval phase.</li>
 * <li>Post processing.</li>
 * <li>Saving to lru.</li>
 * <li>Notifying to the UI for success</li>
 * <p/>
 * </ul>
 * <p/>
 * In any moment the Strategy can notify of an error through {@link Notifiable#notify(Object) NotifyError} interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class StandardNetworkInteractor<R extends Response, E extends Error> extends NetworkInteractor<R, E> {
    /**
     * Creates a new instance of Standard Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    public StandardNetworkInteractor(Postable bus, Loggable log, NetworkDelegate<R, E> delegate) {
        super(bus, log, delegate);
    }

    @Override
    public Object execute() {
        try {
            final R content = ((NetworkDelegate<R, E>) delegate).execute();
            final R processedContent = ((NetworkDelegate<R, E>) delegate).postProcess(content);
            long start = System.currentTimeMillis();
            ((NetworkDelegate<R, E>) delegate).saveToCache(processedContent);
            log.d(TAG, delegate.getInteractorName() + "(Saving):" + (System.currentTimeMillis() - start) + "ms");
            return processedContent;
        } catch (Exception x) {
            final E error = ((NetworkDelegate<R, E>) delegate).composeErrorResponse(x);
            if (loggable && error.isLoggable()) {
                delegate.logErrorResponse(log, x);
            }
            return error;
        }
    }
}
