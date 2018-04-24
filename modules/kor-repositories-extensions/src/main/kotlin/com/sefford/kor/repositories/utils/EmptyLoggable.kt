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
package com.sefford.kor.repositories.utils

import com.sefford.common.interfaces.Loggable

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class EmptyLoggable : Loggable {

    override fun printPerformanceLog(tag: String?, delegateName: String?, start: Long) {

    }

    override fun e(tag: String?, message: String?, exception: Throwable?) {

    }

    override fun d(tag: String?, message: String?) {

    }

}