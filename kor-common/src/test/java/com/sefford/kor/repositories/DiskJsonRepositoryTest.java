/*
 * Copyright (C) 2017 Saúl Díaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sefford.kor.repositories;

import com.google.gson.Gson;
import com.sefford.common.interfaces.Loggable;
import com.sefford.kor.repositories.utils.TestElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
public class DiskJsonRepositoryTest {

    DiskJsonRepository<Integer, TestElement> repository;
    @Mock
    File folder;
    @Mock
    Loggable loggable;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        repository = spy(new DiskJsonRepository<>(folder, new Gson(), loggable, TestElement.class));
        doNothing().when(((DiskJsonRepository) repository)).write(any(TestElement.class));
    }

    @Test
    public void testClearing() throws Exception {
        final File mockedFile1 = mock(File.class);
        final File mockedFile2 = mock(File.class);
        when(folder.listFiles()).thenReturn(new File[]{mockedFile1, mockedFile2});

        repository.clear();

        verify(mockedFile1, times(1)).delete();
        verify(mockedFile2, times(1)).delete();
    }

    @Test
    public void testContainmentWhenTheFileIsNull() throws Exception {
        doReturn(null).when(((DiskJsonRepository) repository)).getFile(0);

        assertFalse(repository.contains(0));
    }

    @Test
    public void testContainmentWhenTheFileDoesNotExist() throws Exception {
        final File mockedFile = mock(File.class);
        doReturn(mockedFile).when(((DiskJsonRepository) repository)).getFile(0);

        assertFalse(repository.contains(0));
    }

    @Test
    public void testContainmentWhenTheFileDoesExist() throws Exception {
        final File mockedFile = prepareFileForDeletion(0);

        assertTrue(repository.contains(0));
    }

    @Test
    public void testDeletion() throws Exception {
        final File mockedFile = prepareFileForDeletion(0);

        repository.delete(0, new TestElement(0));

        verify(mockedFile, times(1)).delete();
    }

    @Test
    public void testMultipleDeletion() throws Exception {
        final File mockedFile1 = prepareFileForDeletion(0);
        final File mockedFile2 = prepareFileForDeletion(1);

        repository.deleteAll(Arrays.asList(new TestElement(0), new TestElement(1)));

        verify(mockedFile1, times(1)).delete();
        verify(mockedFile2, times(1)).delete();
    }

    @Test
    public void testRetrieval() throws Exception {
        final TestElement element = prepareElementForRetrieval(0);

        assertThat(repository.get(0), is(element));
    }

    @Test
    public void testSelectiveRetrieval() throws Exception {
        final TestElement element = prepareElementForRetrieval(0);
        final TestElement element1 = prepareElementForRetrieval(1);
        final TestElement element2 = prepareElementForRetrieval(2);

        final Collection<TestElement> elements = repository.getAll(Arrays.asList(0, 2));

        assertTrue(elements.contains(element));
        assertFalse(elements.contains(element1));
        assertTrue(elements.contains(element2));
    }

    @Test
    public void testFullRetrieval() throws Exception {
        final File file = prepareFileForRetrieval(0);
        final File file1 = prepareFileForRetrieval(1);
        final File file2 = prepareFileForRetrieval(2);
        when(folder.listFiles()).thenReturn(new File[]{file, file1, file2});

        final Collection<TestElement> elements = repository.getAll();

        assertTrue(elements.contains(new TestElement(0)));
        assertTrue(elements.contains(new TestElement(1)));
        assertTrue(elements.contains(new TestElement(2)));
    }

    @Test
    public void testSaving() throws Exception {
        final TestElement element = new TestElement(0);

        repository.save(element);

        verify(((DiskJsonRepository) repository), times(1)).write(element);
    }

    @Test
    public void testMultipleSave() throws Exception {
        final TestElement element = new TestElement(0);
        final TestElement element1 = new TestElement(1);

        repository.saveAll(Arrays.asList(element, element1));

        verify(((DiskJsonRepository) repository), times(1)).write(element);
        verify(((DiskJsonRepository) repository), times(1)).write(element1);
    }

    @Test
    public void testAvailabilityWhenFolderIsNull() throws Exception {
        repository = new DiskJsonRepository<>((File) null, new Gson(), loggable, TestElement.class);

        assertFalse(repository.isAvailable());
    }

    @Test
    public void testAvailabilityWhenFolderDoesNotExist() throws Exception {
        assertFalse(repository.isAvailable());
    }

    @Test
    public void testAvalilabilityWhenFolderDoesExist() throws Exception {
        when(folder.exists()).thenReturn(Boolean.TRUE);

        assertTrue(repository.isAvailable());
    }

    private File prepareFileForDeletion(int id) {
        final File mockedFile1 = mock(File.class);
        when(mockedFile1.exists()).thenReturn(Boolean.TRUE);
        doReturn(mockedFile1).when(((DiskJsonRepository) repository)).getFile(id);
        return mockedFile1;
    }

    private TestElement prepareElementForRetrieval(int id) {
        final File mockedFile = mock(File.class);
        final TestElement element = new TestElement(id);
        when(mockedFile.exists()).thenReturn(Boolean.TRUE);
        doReturn(mockedFile).when(((DiskJsonRepository) repository)).getFile(id);
        doReturn(element).when(((DiskJsonRepository) repository)).read(mockedFile);
        return element;
    }

    private File prepareFileForRetrieval(int id) {
        final File mockedFile = mock(File.class);
        final TestElement element = new TestElement(id);
        when(mockedFile.exists()).thenReturn(Boolean.TRUE);
        doReturn(mockedFile).when(((DiskJsonRepository) repository)).getFile(id);
        doReturn(element).when(((DiskJsonRepository) repository)).read(mockedFile);
        return mockedFile;
    }

}