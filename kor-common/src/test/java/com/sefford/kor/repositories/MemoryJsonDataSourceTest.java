package com.sefford.kor.repositories;

import com.google.gson.Gson;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MemoryJsonDataSourceTest {

    static final int EXPECTED_MULTIPLE_ELEMENTS_LIST = 2;

    MemoryJsonDataSource<Integer, TestElement> dataSource;

    @Before
    public void setUp() throws Exception {
        dataSource = new MemoryJsonDataSource<>(new Gson(), TestElement.class);
    }

    @Test
    public void clears() throws Exception {
        dataSource.save(new TestElement(0));

        dataSource.clear();

        assertThat(dataSource.contains(0), is(Boolean.FALSE));
    }

    @Test
    public void contains() throws Exception {
        dataSource.save(new TestElement(0));

        assertThat(dataSource.contains(0), is(Boolean.TRUE));
    }

    @Test
    public void doesNotContain() throws Exception {
        assertThat(dataSource.contains(0), is(Boolean.FALSE));
    }

    @Test
    public void deletes() throws Exception {
        dataSource.save(new TestElement(0));

        dataSource.delete(0, null);

        assertThat(dataSource.contains(0), is(Boolean.FALSE));
    }

    @Test
    public void deletesAll() throws Exception {
        dataSource.save(new TestElement(0));
        dataSource.save(new TestElement(1));

        dataSource.deleteAll(Arrays.asList(new TestElement(0), new TestElement(1)));

        assertThat(dataSource.contains(0), is(Boolean.FALSE));
        assertThat(dataSource.contains(1), is(Boolean.FALSE));
    }

    @Test
    public void retrieves() throws Exception {
        dataSource.save(new TestElement(0));

        TestElement testElement = dataSource.get(0);

        assertThat(testElement.getId(), is(0));
    }

    @Test
    public void retrievesSeveralIds() throws Exception {
        dataSource.save(new TestElement(0));
        dataSource.save(new TestElement(1));

        final ArrayList<TestElement> elements = new ArrayList<>(dataSource.getAll(Arrays.asList(0, 1)));

        assertThat(elements.size(), is(EXPECTED_MULTIPLE_ELEMENTS_LIST));
        assertThat(elements.get(0).getId(), is(0));
        assertThat(elements.get(1).getId(), is(1));
    }

    @Test
    public void retrievesAll() throws Exception {
        dataSource.save(new TestElement(0));
        dataSource.save(new TestElement(1));

        final ArrayList<TestElement> elements = new ArrayList<>(dataSource.getAll());

        assertThat(elements.size(), is(EXPECTED_MULTIPLE_ELEMENTS_LIST));
        assertThat(elements.get(0).getId(), is(0));
        assertThat(elements.get(1).getId(), is(1));
    }

    @Test
    public void saves() throws Exception {
        dataSource.save(new TestElement(0));

        assertThat(dataSource.contains(0), is(Boolean.TRUE));
    }

    @Test
    public void saveAll() throws Exception {
        dataSource.saveAll(Arrays.asList(new TestElement(0), new TestElement(1)));

        assertThat(dataSource.contains(0), is(Boolean.TRUE));
        assertThat(dataSource.contains(1), is(Boolean.TRUE));
    }

    @Test
    public void isAvailable() throws Exception {
        assertThat(dataSource.isAvailable(), is(Boolean.TRUE));
    }
}