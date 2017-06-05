package com.sefford.kor.repositories.utils;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Updateable;

/**
 * Created by sefford on 6/5/17.
 */
public class TestElement implements RepoElement<Integer>, Updateable<TestElement> {

    final int id;

    public TestElement(int id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public TestElement update(TestElement other) {
        return this;
    }

    @Override
    public boolean equals(Object that) {
        return id == ((TestElement) that).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
