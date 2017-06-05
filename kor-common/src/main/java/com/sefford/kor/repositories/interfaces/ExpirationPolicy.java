package com.sefford.kor.repositories.interfaces;

/**
 * Created by sefford on 6/2/17.
 */
public interface ExpirationPolicy<K> {

    boolean isExpired(K id);

    void notifyDeleted(K id);

    void notifyCreated(K id);

    void clear();
}
