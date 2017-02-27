package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.Notifiable;
import com.sefford.kor.interactors.interfaces.OptimisticDelegate;
import com.sefford.kor.responses.Response;

/**
 * Standard Network Interactor that performs the Request Process in this order:
 * * <ul>
 * <li>Performing Changes to Cache</li>
 * <li>Network Retrieval phase.</li>
 * <li>Post processing.</li>
 * <li>Notifying to the UI for success</li>
 * <p/>
 * </ul>
 * <p/>
 * In any moment the Strategy can notify of an error through {@link Notifiable#notify(Object) NotifyError} interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public class OptimisticNetworkInteractor<R extends Response, E extends Error> extends Interactor<R, E> {

    /**
     * Creates a new instance of Base Network Interactor
     *
     * @param bus      Notification Facility
     * @param log      Logging facilities
     * @param delegate Request to execute
     */
    public OptimisticNetworkInteractor(Postable bus, Loggable log, OptimisticDelegate<R, E> delegate) {
        super(bus, log, delegate);
    }

    @Override
    public Object execute() {
        try {
            ((OptimisticDelegate<R, E>) delegate).performCacheChanges();
            final R content = ((OptimisticDelegate<R, E>) delegate).execute();
            final R processedContent = ((OptimisticDelegate<R, E>) delegate).postProcess(content);
            long start = System.currentTimeMillis();
            log.d(TAG, delegate.getInteractorName() + "(Saving):" + (System.currentTimeMillis() - start) + "ms");
            return processedContent;
        } catch (Exception x) {
            final E error = ((OptimisticDelegate<R, E>) delegate).composeErrorResponse(x);
            ((OptimisticDelegate<R, E>) delegate).revertCacheStatus();
            if (loggable && error.isLoggable()) {
                delegate.logErrorResponse(log, x);
            }
            return error;
        }
    }
}
