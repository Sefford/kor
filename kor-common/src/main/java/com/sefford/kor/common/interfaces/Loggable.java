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
package com.sefford.kor.common.interfaces;

/**
 * Loggable interface will act as a way to extend the developer's logging facilities to adapt to Kor
 * interfaces.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface Loggable {


    void d(String tag, String message);

    void printPerformanceLog(String tag, String requestName, long start);

    void e(String tag, String requestName, Throwable tr);
}
