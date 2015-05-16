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
package com.sefford.kor.interactors.interfaces;

import com.sefford.kor.errors.Error;
import com.sefford.kor.responses.Response;

/**
 * Interface for managing Cache Interactors.
 * <p/>
 * A cache delegate is intended to be able to check if the cache is valid then retrieve it if it is.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface CacheDelegate<R extends Response, E extends Error> extends Delegate<R, E> {

    /**
     * Identifies if a CacheRequest is valid or not.
     *
     * @return TRUE if the Cache is up-to-date and usable.
     */
    boolean isCacheValid();
}