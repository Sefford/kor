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
package com.sefford.kor.repositories.interfaces;

/**
 * Expiration policy interface to apply to {@link com.sefford.kor.repositories.ExpirationRepository ExpirationRepository} API.
 */
public interface ExpirationPolicy<K> {

    /**
     * Returns if an element is expired
     *
     * @param id ID to check if the element is expired
     * @return TRUE if expired, FALSE otherwise
     */
    boolean isExpired(K id);

    /**
     * Lets the policy to know that an element has been signalling to be deleted and the policy for such element needs
     * to be removed.
     *
     * @param id ID of the element whose policy can be deleted
     */
    void notifyDeleted(K id);

    /**
     * Lets the policy to know that an element has been added and the policy for such element needs
     * to be removed.
     * <p>
     * Take into account that an element might be saved several times to be updated, and you should update the policy
     * accordingly and avoid duplications.
     *
     * @param id ID of the element whose policy requires to be created
     */
    void notifyCreated(K id);

    /**
     * Signals that the Repository has been cleared and all policies can be flushed out.
     */
    void clear();
}
