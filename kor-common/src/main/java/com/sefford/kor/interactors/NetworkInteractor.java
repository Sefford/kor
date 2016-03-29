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
import com.sefford.kor.responses.Response;

/**
 * Base Network Interactor hierarchy line.
 * <p/>
 * This abstract class starts notifying the errors to the UI in preparation for Network Interaction.
 * <p/>
 * This hierarchy of classes will delegate the work to {@link com.sefford.kor.interactors.interfaces.NetworkDelegate NetworkDelegate} implementations.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class NetworkInteractor<R extends Response, E extends Error> extends Interactor<R, E> {
    /**
     * Creates a new instance of Base Network Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    protected NetworkInteractor(Postable bus, Loggable log, NetworkDelegate<R, E> delegate) {
        super(bus, log, delegate);
    }

}
