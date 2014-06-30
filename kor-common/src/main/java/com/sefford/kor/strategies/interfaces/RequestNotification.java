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
package com.sefford.kor.strategies.interfaces;

import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.responses.ResponseInterface;

/**
 * Strategy notification interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface RequestNotification<R extends ResponseInterface, E extends ErrorInterface> {

    /**
     * Notifies of the finishing of the request
     *
     * @param content Processed response
     */
    void notifySuccess(R content);

    /**
     * Notifies of an error on the request
     *
     * @param error Error
     */
    void notifyError(E error);
}

