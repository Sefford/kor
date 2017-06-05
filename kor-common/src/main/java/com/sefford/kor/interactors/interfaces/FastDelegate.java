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

import com.sefford.kor.responses.Response;

/**
 * Interface for Interactors that is intended to be used with delegates that require a fast memory save method.
 * <p/>
 * It should be used by a Repository that implements {@link com.sefford.kor.repositories.interfaces.FastRepository FastRepository}
 * interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface FastDelegate<R extends Response> {

    /**
     * Performs a Fast save.
     * <p/>
     * A fast save is a facility to provide a secondary, quick method of storing information on a
     * Repository without affecting the rest.
     * <p/>
     * For more info refer to {@link com.sefford.kor.repositories.interfaces.FastRepository FastRepository} documentation
     *
     * @param response Response element to save to memory lru.
     * @return Saved response.
     */
    R fastSave(R response);
}
