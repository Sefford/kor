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
package com.sefford.kor.errors;

/**
 * Generic Error handling for Requests.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class BaseError {

    /**
     * Http default error for when the error was null or could not be set
     */
    public static final int UNANALYZED_CODE = -1;
    /**
     * Default message for when the error could not be set
     */
    public static final String UNANALYZED_ERROR_MESSAGE = "Exception deserializing error";
    /**
     * API Error Info
     */
    protected ErrorInfo info;

    /**
     * Creates an Error from an Exception
     *
     * @param exception Exception
     */
    protected BaseError(Exception exception) {
        info = new ErrorInfo();
        info.statusCode = UNANALYZED_CODE;
        info.message = exception.getMessage();
        info.userError = UNANALYZED_ERROR_MESSAGE;
    }

    /**
     * Returns the API info code
     *
     * @return Status code of the API, 0 if there was a problem deserializing
     */
    public int getStatusCode() {
        return info != null ? info.statusCode : UNANALYZED_CODE;
    }

    /**
     * Returns the human readable error
     *
     * @return Human redeable error or generic string
     */
    public String getUserError() {
        return info != null ? info.userError : UNANALYZED_ERROR_MESSAGE;
    }

    /**
     * Returns the  inner message error
     *
     * @return Mesasge or generic string
     */
    public String getMessage() {
        return info != null ? info.message : UNANALYZED_ERROR_MESSAGE;
    }

    /**
     * Api error information class
     *
     * @author Saul Diaz <sefford@gmail.com>
     */
    class ErrorInfo {
        /**
         * Api status code
         */
        int statusCode;
        /**
         * Human readable error
         */
        String userError;
        /**
         * Inner message
         */
        String message;
    }
}
