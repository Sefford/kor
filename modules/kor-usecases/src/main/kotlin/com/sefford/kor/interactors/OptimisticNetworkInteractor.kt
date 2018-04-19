package com.sefford.kor.interactors

import com.sefford.common.interfaces.Loggable
import com.sefford.common.interfaces.Postable
import com.sefford.kor.interactors.delegates.Notifiable
import com.sefford.kor.interactors.delegates.OptimisticDelegate
import com.sefford.kor.usecases.components.Response
import com.sefford.kor.usecases.components.Error
/**
 * Standard Network Interactor that performs the Request Process in this order:
 * *
 *  * Performing Changes to Cache
 *  * Network Retrieval phase.
 *  * Post processing.
 *  * Notifying to the UI for success
 *
 *
 *
 *
 *
 * In any moment the Strategy can notify of an error through [NotifyError][Notifiable.notify] interface.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
class OptimisticNetworkInteractor<R : Response, E : Error>
/**
 * Creates a new instance of Base Network Interactor
 *
 * @param bus      Notification Facility
 * @param log      Logging facilities
 * @param delegate Request to execute
 */
(bus: Postable, log: Loggable, delegate: OptimisticDelegate<R, E>) : Interactor<R, E>(bus, log, delegate) {

    override fun execute(): Any {
        val start = System.currentTimeMillis()
        try {
            delegate.startPerformanceLog(log)
            (delegate as OptimisticDelegate<R, E>).performCacheChanges()
            val content = delegate.execute()
            val processedContent = delegate.postProcess(content)
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return processedContent
        } catch (x: Exception) {
            val error = (delegate as OptimisticDelegate<R, E>).composeErrorResponse(x)
            delegate.revertCacheStatus()
            if (loggable && error.loggable) {
                delegate.logErrorResponse(log, x)
            }
            delegate.endPerformanceLog(log, System.currentTimeMillis() - start)
            return error
        }

    }
}
