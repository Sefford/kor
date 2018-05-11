/*
 * Copyright (C) 2018 Saúl Díaz
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
package com.sefford.kor.usecases.components

/**
 * Performance module interface.
 *
 * Executes {@link start start} at the beginning of the execution and {@link end end} at the end of the use case.
 *
 * Take into account that some implementations may require to save a t0 in start and calculate the delta in end.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface PerformanceModule {

    /**
     * Trace name of the instrumented use case
     */
    val name: String

    /**
     * Signals that the start of the measure
     *
     * @param traceId defaults to {@link name name}
     */
    fun start(traceId: String = name)

    /**
     * Signals the end of the measure
     *
     * @param traceId defaults to {@link name name}
     */
    fun end(traceId: String = name)
}