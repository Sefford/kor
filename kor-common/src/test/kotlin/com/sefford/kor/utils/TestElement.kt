/*
 * Copyright (C) 2017 Saúl Díaz
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
package com.sefford.kor.utils

import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Updateable

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
open class TestElement(override val id: Int) : RepoElement<Int>, Updateable<TestElement> {

    override fun update(other: TestElement): TestElement {
        return this
    }

    override fun equals(that: Any?): Boolean {
        return id == (that as TestElement).id
    }

    override fun hashCode(): Int {
        return id
    }
}
