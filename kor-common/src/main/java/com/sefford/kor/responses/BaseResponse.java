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
package com.sefford.kor.responses;

/**
 * Base Response
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public abstract class BaseResponse {

    /**
     * Flag indicating the request was a success or not
     */
    protected boolean success;

    /**
     * Origin of the data
     */
    protected boolean fromNetwork;

    /**
     * Returns if the Request was successful
     *
     * @return TRUE if it was, FALSE otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success of the Request
     *
     * @param success Flag indicating the success of the request
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFromNetwork() {
        return fromNetwork;
    }

    public void setFromNetwork(boolean fromNetwork) {
        this.fromNetwork = fromNetwork;
    }

}
