package com.sefford.kor.repositories;

import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Updateable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for RealmRepository
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
@RunWith(RobolectricTestRunner.class)
public class RealmRepositoryTest {

    static final String EXPECTED_ID_1 = "1";
    static final String EXPECTED_ID_2 = "2";
    static final String EXPECTED_ID_3 = "3";
    public static final String ID_PARAM = "id";

    TestRepository repository;

    @Mock
    Realm realm;
    @Mock
    TestElement element1;
    @Mock
    TestElement element2;
    @Mock
    TestElement element3;
    @Mock
    RealmQuery<TestElement> query;
    @Mock
    RealmResults<TestElement> results;

    Class<TestElement> clazz;
    List<String> allIds;
    List<TestElement> allElements;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        clazz = TestElement.class;

        when(realm.where(clazz)).thenReturn(query);
        when(realm.allObjects(clazz)).thenReturn(results);
        when(realm.createObject(clazz)).thenReturn(element1)
                .thenReturn(element2)
                .thenReturn(element3);

        when(query.equalTo(eq(ID_PARAM), anyString())).thenReturn(query);
        when(query.or()).thenReturn(query);

        when(query.findAll()).thenReturn(results);

        when(element1.getId()).thenReturn(EXPECTED_ID_1);
        when(element1.update(element1)).thenReturn(element1);
        when(element2.getId()).thenReturn(EXPECTED_ID_2);
        when(element2.update(element2)).thenReturn(element2);
        when(element3.getId()).thenReturn(EXPECTED_ID_3);
        when(element3.update(element3)).thenReturn(element3);

        allIds = new ArrayList<String>();
        allIds.add(EXPECTED_ID_1);
        allIds.add(EXPECTED_ID_2);
        allIds.add(EXPECTED_ID_3);

        allElements = new ArrayList<TestElement>();
        allElements.add(element1);
        allElements.add(element2);
        allElements.add(element3);

        repository = spy(new TestRepository(realm, clazz));
    }

    @Test
    public void testContainsInMemory() throws Exception {
        repository.contains(EXPECTED_ID_1);

        verify(repository, times(1)).contains(EXPECTED_ID_1);
    }

    @Test
    public void testGetFromMemory() throws Exception {
        repository.getFromMemory(EXPECTED_ID_1);

        verify(repository, times(1)).get(EXPECTED_ID_1);
    }

    @Test
    public void testGetAllFromMemory() throws Exception {
        repository.getAllFromMemory(allIds);

        verify(repository, times(1)).getAll(allIds);
    }

    @Test
    public void testSaveInMemory() throws Exception {
        when(repository.prepareElementForSaving(element1)).thenReturn(element1);

        repository.saveInMemory(element1);

        verify(repository, times(1)).save(element1);
    }

    @Test
    public void testSaveAllInMemory() throws Exception {
        when(repository.prepareElementForSaving(element1)).thenReturn(element1);
        when(repository.prepareElementForSaving(element2)).thenReturn(element2);
        when(repository.prepareElementForSaving(element3)).thenReturn(element3);

        repository.saveAllInMemory(allElements);

        verify(repository, times(1)).saveAll(allElements);
    }

    @Test
    public void testClear() throws Exception {
        repository.clear();

        verify(realm, times(1)).clear(clazz);
    }

    @Test
    public void testRepositoryContains() throws Exception {
        when(query.findFirst()).thenReturn(element1);

        assertTrue(repository.contains(EXPECTED_ID_1));
    }

    @Test
    public void testRepositoryDoesNotContain() throws Exception {
        when(query.findFirst()).thenReturn(null);

        assertFalse(repository.contains(EXPECTED_ID_1));
    }

    @Test
    public void testDelete() throws Exception {
        repository.delete(EXPECTED_ID_1, element1);

        verify(repository, times(1)).deleteAll(Matchers.<List<TestElement>>any());
    }

    @Test
    public void testDeleteAll() throws Exception {
        repository.deleteAll(allElements);

        verify(query, times(1)).findAll();
        verify(results, times(1)).clear();
    }

    @Test
    public void testGet() throws Exception {
        when(query.findFirst()).thenReturn(element1);

        assertEquals(element1, repository.get(EXPECTED_ID_1));
    }

    @Test
    public void testGetAll() throws Exception {
        when(query.findAll()).thenReturn(results);

        assertEquals(results, repository.getAll());
    }

    @Test
    public void testGetAllPartial() throws Exception {
        when(query.findAll()).thenReturn(results);

        assertEquals(results, repository.getAll(allIds));
    }

    @Test
    public void testPrepareQueryWithSingleElement() throws Exception {
        List<String> ids = new ArrayList<String>();
        ids.add(EXPECTED_ID_1);

        assertEquals(query, repository.prepareQuery(ids));

        InOrder order = inOrder(query);
        order.verify(query, times(1)).equalTo(ID_PARAM, EXPECTED_ID_1);
        order.verify(query, never()).or();
    }

    @Test
    public void testPrepareQueryWithMultipleElements() throws Exception {
        List<String> ids = new ArrayList<String>();
        ids.add(EXPECTED_ID_1);
        ids.add(EXPECTED_ID_2);
        ids.add(EXPECTED_ID_3);

        assertEquals(query, repository.prepareQuery(ids));

        InOrder order = inOrder(query);
        order.verify(query, times(1)).equalTo(ID_PARAM, EXPECTED_ID_1);
        order.verify(query, times(1)).or();
        order.verify(query, times(1)).equalTo(ID_PARAM, EXPECTED_ID_2);
        order.verify(query, times(1)).or();
        order.verify(query, times(1)).equalTo(ID_PARAM, EXPECTED_ID_3);
        order.verify(query, never()).or();

    }

    @Test
    public void testSave() throws Exception {
        when(repository.prepareElementForSaving(element1)).thenReturn(element1);
        when(realm.createObject(clazz)).thenReturn(element1);

        assertEquals(element1, repository.save(element1));

        InOrder order = inOrder(realm, repository);
        order.verify(realm, times(1)).beginTransaction();
        order.verify(repository, times(1)).prepareElementForSaving(element1);
        order.verify(realm, times(1)).commitTransaction();


    }

    @Test
    public void testSaveAll() throws Exception {
        when(repository.prepareElementForSaving(element1)).thenReturn(element1);
        when(repository.prepareElementForSaving(element2)).thenReturn(element2);
        when(repository.prepareElementForSaving(element3)).thenReturn(element3);

        repository.saveAll(allElements);

        InOrder order = inOrder(realm, repository);
        order.verify(realm, times(1)).beginTransaction();
        order.verify(repository, times(1)).prepareElementForSaving(element1);
        order.verify(repository, times(1)).prepareElementForSaving(element2);
        order.verify(repository, times(1)).prepareElementForSaving(element3);
        order.verify(realm, times(1)).commitTransaction();
    }

    @Test
    public void testPrepareElementForSavingWithNonExistingElement() throws Exception {
        when(repository.get(anyString())).thenReturn(null);

        repository.prepareElementForSaving(element1);

        InOrder order = inOrder(realm, element1);
        order.verify(realm, times(1)).createObject(clazz);
        order.verify(element1, times(1)).update(element1);
    }

    @Test
    public void testPrepareElementForSavingWithExistingElement() throws Exception {
        when(repository.get(anyString())).thenReturn(element1);

        repository.prepareElementForSaving(element1);

        InOrder order = inOrder(realm, element1);
        order.verify(realm, never()).createObject(clazz);
        order.verify(element1, times(1)).update(element1);
    }

    @Test
    public void testIsAvailable() throws Exception {
        assertTrue(repository.isAvailable());
    }

    @Test
    public void testIsNotAvailable() throws Exception {
        repository = new TestRepository(null, clazz);
        assertFalse(repository.isAvailable());
    }

    class TestElement extends RealmObject implements RepoElement<String>, Updateable<TestElement> {

        @Override
        public String getId() {
            return "";
        }

        @Override
        public TestElement update(TestElement other) {
            return this;
        }
    }

    class TestRepository extends RealmRepository<String, TestElement> {

        public TestRepository(Realm realm, Class<TestElement> clazz) {
            super(realm, clazz);
        }

        @Override
        protected TestElement update(TestElement oldElement, TestElement newElement) {
            return oldElement;
        }

        @Override
        protected String getId(TestElement element) {
            return element.getId();
        }
    }
}