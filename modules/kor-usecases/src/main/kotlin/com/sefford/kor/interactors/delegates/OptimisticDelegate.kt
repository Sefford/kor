package com.sefford.kor.interactors.delegates

import com.sefford.kor.usecases.components.Error
import com.sefford.kor.usecases.components.Response

/**
 * Interface for performing interactions to the network with optimistic results for lru.
 *
 *
 * A delegate is intended to go through at least four phases
 *
 *
 *
 *  * Performing changes
 *  * Network Retrieval (execution) phase.
 *  * Post processing.
 *  *
 *
 *
 *
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
interface OptimisticDelegate<R : Response, E : Error> : Delegate<R, E> {

    /**
     * Performs expected changes to the lru before the API call is executed
     */
    fun performCacheChanges()

    /**
     * Does a processing of the Response.
     *
     *
     * In the default implementation this is a chance for the developer to execute code regarding the response.
     * This might range from crossing information from the lru to the fetched data to validate it.
     *
     *
     * While it is not an expected behavior, the developer can still have a chance to throw an exception.
     *
     * @param response Response response to Process
     * @return A modified Response response
     */
    fun postProcess(response: R): R

    /**
     * Opportunity to revert changes to the lru
     */
    fun revertCacheStatus()
}
