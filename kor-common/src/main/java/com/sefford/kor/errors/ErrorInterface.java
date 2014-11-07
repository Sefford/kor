package com.sefford.kor.errors;

/**
 * Generic Error interface for delegates
 * <p/>
 * The Base Error encapsulates the bare information to identify an error, this is, an Error Code which
 * can be used for Error identification, a human-readable string error for information purposes and
 * an internal string in order to identify it to logger.
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
public interface ErrorInterface {

    /**
     * Returns the API info code
     *
     * @return Status code of the API,
     */
    int getStatusCode();

    /**
     * Returns the human readable error
     *
     * @return Human redeable error or generic string
     */
    String getUserError();

    /**
     * Returns the  inner message error.
     *
     * @return Message for logging purposes
     */
    String getMessage();
}
