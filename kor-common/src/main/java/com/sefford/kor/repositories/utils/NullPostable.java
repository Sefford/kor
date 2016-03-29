package com.sefford.kor.repositories.utils;

import com.sefford.common.interfaces.Postable;

/**
 * Created by sefford on 29/03/16.
 */
public class NullPostable implements Postable {

    public static final NullPostable INSTANCE = new NullPostable();

    private NullPostable() {
    }

    @Override
    public void post(Object event) {

    }
}
