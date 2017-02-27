package com.sefford.kor.interactors.interfaces;

import com.sefford.kor.errors.Error;
import com.sefford.kor.responses.Response;

/**
 * Interface for performing interactions to the network with optimistic results for cache.
 * <p/>
 * A delegate is intended to go through at least four phases
 * <p/>
 * <ul>
 * <li>Performing changes</li>
 * <li>Network Retrieval (execution) phase.</li>
 * <li>Post processing.</li>
 * <li></li>
 * <p/>
 * </ul>
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface OptimisticDelegate<R extends Response, E extends Error> extends Delegate<R, E> {

    /**
     * Performs expected changes to the cache before the API call is executed
     */
    void performCacheChanges();

    /**
     * Does a processing of the Response.
     * <p/>
     * In the default implementation this is a chance for the developer to execute code regarding the response.
     * This might range from crossing information from the cache to the fetched data to validate it.
     * <p/>
     * While it is not an expected behavior, the developer can still have a chance to throw an exception.
     *
     * @param response Response response to Process
     * @return A modified Response response
     */
    R postProcess(R response);

    /**
     * Opportunity to revert changes to the cache
     */
    void revertCacheStatus();
}
