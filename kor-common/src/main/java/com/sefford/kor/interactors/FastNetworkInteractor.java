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
import com.sefford.kor.interactors.interfaces.FastDelegate;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.responses.Response;

/**
 * Specialization of a Network Interactor to support fast lru saving.
 * <p/>
 * This strategy adds a first fast save on the repository to have the information already on the repository,
 * then notifies the UI. After the notification a standard full-save is performed on the repository
 * in the background.
 * <p/>
 * An error condition will only be thrown if there is any issue before the notification. After it, it
 * will only be logged to avoid error duplication.
 *
 * @author Saul Diaz <sefford@gmail.com>
 * @deprecated Incompatible with the latest versions
 */
@Deprecated
public class FastNetworkInteractor<R extends Response, E extends Error> extends NetworkInteractor<R, E> {

    /**
     * Creates a new instance of Fast Saving Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    public FastNetworkInteractor(Postable bus, Loggable log, NetworkDelegate<R, E> delegate) {
        super(bus, log, delegate);
    }

    @Override
    public void run() {
        try {
            final R content = ((NetworkDelegate<R, E>) delegate).execute();
            final R processedContent = ((NetworkDelegate<R, E>) delegate).postProcess(content);
            final R savedMemoryContent = ((FastDelegate<R>) delegate).fastSave(processedContent);
            notify(savedMemoryContent);
            try {
                long start = System.currentTimeMillis();
                ((NetworkDelegate<R, E>) delegate).saveToCache(savedMemoryContent);
                log.printPerformanceLog(TAG, delegate.getInteractorName(), start);
            } catch (Exception x) {
                // On the inner exception we just do not notify the error
                log.e(TAG, delegate.getInteractorName(), x);
            }
        } catch (Exception x) {
            final E error = ((NetworkDelegate<R, E>) delegate).composeErrorResponse(x);
            if (error.isLoggable()) {
                log.e(TAG, delegate.getInteractorName(), x);
            }
            notify(error);
        }
    }

    @Override
    public Object execute() {
        throw new IllegalThreadStateException("Cannot be executed syncronously");
    }

}
