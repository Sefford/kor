/*
 * Copyright (C) 2015 Saúl Díaz
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

import java.util.concurrent.ThreadPoolExecutor;

/**
 * This is the Standalone interactor. Decorates one of the simple Interactor strategies.
 * <p/>
 * It is meant to supersede old {@link com.sefford.kor.providers.interfaces.Executor Executor} in basic situations
 * by providing an Interactor that can function on its own. It will help on reducing constructor signatures on modules
 * that require a small number of use cases.
 * <p/>
 * For more fine-grained thread management, Executor implementations are advised.
 *
 * @author Saul Diaz Gonzalez<sefford@gmail.com>
 */
public abstract class StandaloneInteractor<O extends Object> {

    /**
     * Execution element for the Interactor
     */
    final ThreadPoolExecutor executor;

    /**
     * Logging facilities for the interactor
     */
    protected final Loggable log;

    /**
     * Creates a new instance of the Standalone Interactor.
     *
     * @param log      Logging facilities
     * @param executor Execution element
     */
    protected StandaloneInteractor(Loggable log, ThreadPoolExecutor executor) {
        this.executor = executor;
        this.log = log;
    }

    /**
     * Instantiates the new {@link Interactor Interactor} that will be executed on {@link #execute(Postable, Object) execute}
     * method.
     *
     * @param bus     Postable where the {@link com.sefford.kor.responses.Response responses} and the {@link com.sefford.kor.errors.Error errors} will come through
     * @param options Extension element to help the Interactor to configure itself
     * @return Interactor instance to be executed
     */
    protected abstract Interactor instantiateInteractor(Postable bus, O options);

    /**
     * Executes the Interactor
     *
     * @param bus     Postable where the {@link com.sefford.kor.responses.Response responses} and the {@link com.sefford.kor.errors.Error errors} will come through
     * @param options Extension element to help the Interactor to configure itself
     */
    public void execute(Postable bus, O options) {
        executor.execute(instantiateInteractor(bus, options));
    }
}
