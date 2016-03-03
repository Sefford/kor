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
import com.sefford.kor.interactors.interfaces.UpdateableDelegate;
import com.sefford.kor.responses.Response;

/**
 * Updateable Interactor, intended for networking. Keeps executing a phase of polling until it decides
 * it has to stop.
 * <p/>
 * Each of the loops will terminate in a call to {@link com.sefford.kor.interactors.interfaces.InteractorNotification#notifySuccess(Response) notifySuccess}
 * if no unexpected condition happened.
 * <p/>
 * Otherwise, it will produce a {@link com.sefford.kor.interactors.interfaces.InteractorNotification#notifyError(Error)}  notifyError} response.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class UpdateableInteractor<R extends Response, E extends Error> extends NetworkInteractor<R, E> {

    /**
     * Creates a new instance of Updateable Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    public UpdateableInteractor(Postable bus, Loggable log, UpdateableDelegate<R, E> delegate) {
        super(bus, log, delegate);
    }

    @Override
    public void run() {
        try {
            while (((UpdateableDelegate) delegate).keepLooping()) {
                final R content = ((NetworkDelegate<R, E>) delegate).execute();
                final R processedContent = ((NetworkDelegate<R, E>) delegate).postProcess(content);
                long start = System.currentTimeMillis();
                ((NetworkDelegate<R, E>) delegate).saveToCache(processedContent);
                log.d(TAG, delegate.getInteractorName() + "(Saving):" + (System.currentTimeMillis() - start) + "ms");
                notifySuccess(processedContent);
            }
        } catch (Exception x) {
            final E error = ((NetworkDelegate<R, E>) delegate).composeErrorResponse(x);
            if (error.isLoggable()) {
                log.e(TAG, delegate.getInteractorName(), x);
            }
            notifyError(error);
        }
    }
}