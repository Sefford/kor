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

/**
 * Delegate Identification Interface.
 * <p/>
 * The lowest common facility of the delegates. This is a convenience interface to let the delegates
 * have an unified way of identifying themselves.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface InteractorIdentification {
    /**
     * Returns delegate name.
     * <p/>
     * Depending on the information held by the Request object. This method can return a customized
     * name with custom parameters to improve logging information.
     *
     * @return String with delegate name.
     */
    String getInteractorName();
}
