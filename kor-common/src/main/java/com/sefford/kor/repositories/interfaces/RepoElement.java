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
package com.sefford.kor.repositories.interfaces;

/**
 * Repo Element interface to work with the {@link com.sefford.kor.repositories.interfaces.Repository Repositories}.
 * <p/>
 * It is an interface that all elements intended to work with repositories have to implement, as it is
 * a simple way of providing a key - value interface to the model objects.
 * <p/>
 * The underlying implementation can rely on a unique ID of the element or the hashCode itself.
 *
 * @author Saul Diaz<sefford@gmail.com>
 */
public interface RepoElement<K> {

    /**
     * Gets the ID key of the element
     *
     * @return ID Key of the element.
     */
    K getId();
}
