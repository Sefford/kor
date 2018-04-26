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
package com.sefford.kor.repositories

import arrow.core.Right
import com.nhaarman.mockito_kotlin.whenever
import com.sefford.kor.repositories.components.CacheFolder
import com.sefford.kor.repositories.components.RepositoryError
import com.sefford.kor.test.TestElement
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import java.io.File

/**
 * @author Saul Diaz <sefford@gmail.com>
 */
class DiskJsonDataSourceTest {

    lateinit var repository: DiskJsonDataSource<Int, TestElement>
    @Mock
    lateinit var folder: CacheFolder<Int>
    @Mock
    internal lateinit var dataHandler: DiskJsonDataSource.DataHandler<Int, TestElement>

    @Before
    fun setUp() {
        initMocks(this)

        repository = DiskJsonDataSource(folder, dataHandler)
        whenever(folder.exists()).thenReturn(true)
    }

    @Test
    fun `the files should be deleted if the repository is cleared`() {
        val mockedFile1 = mock(File::class.java)
        val mockedFile2 = mock(File::class.java)

        whenever(folder.files()).thenReturn(arrayOf(mockedFile1, mockedFile2))

        repository.clear()

        verify(mockedFile1, times(1)).delete()
        verify(mockedFile2, times(1)).delete()
    }

    @Test
    fun `should report not ready if the folder does not exist`() {
        whenever(folder.exists()).thenReturn(false)

        assertFalse(repository.isReady)
    }

    @Test
    fun `should report ready if the folder is healthy`() {
        assertTrue(repository.isReady)
    }

    @Test
    fun `contains should return false when there is a problem with the underlying file`() {
        doReturn(null).whenever<CacheFolder<Int>>(folder).getFile(0)

        assertFalse(repository.contains(0))
    }

    @Test
    fun `contains should return false when file is reported to not existing`() {
        val mockedFile = mock(File::class.java)
        doReturn(mockedFile).whenever<CacheFolder<Int>>(folder).getFile(0)

        assertFalse(repository.contains(0))
    }

    @Test
    fun `contains should return true when the file exist`() {
        createExistingFile(0)

        assertTrue(repository.contains(0))
    }

    @Test
    fun `should delete the file when call to delete`() {
        val mockedFile = createExistingFile(0)

        repository.delete(0, TestElement(0))

        verify(mockedFile, times(1)).delete()
    }

    @Test
    fun `should delete files in batch via varargs`() {
        val mockedFile1 = createExistingFile(0)
        val mockedFile2 = createExistingFile(1)

        repository.delete(TestElement(0), TestElement(1))

        verify(mockedFile1, times(1)).delete()
        verify(mockedFile2, times(1)).delete()
    }

    @Test
    fun `should delete files in batch via list`() {
        val mockedFile1 = createExistingFile(0)
        val mockedFile2 = createExistingFile(1)

        repository.delete(listOf(TestElement(0), TestElement(1)))

        verify(mockedFile1, times(1)).delete()
        verify(mockedFile2, times(1)).delete()
    }

    @Test
    fun `should delete files in batch via iterator`() {
        val mockedFile1 = createExistingFile(0)
        val mockedFile2 = createExistingFile(1)

        repository.delete(listOf(TestElement(0), TestElement(1)).iterator())

        verify(mockedFile1, times(1)).delete()
        verify(mockedFile2, times(1)).delete()
    }

    @Test
    fun `should be able to retrieve an existing element`() {
        createRetrievableTestElement(0)

        assertThat(repository[0].right().get().id, `is`(0))
    }

    @Test
    fun `should return NotFound error if element does not exist`() {
        assertTrue(repository[0].left().get() is RepositoryError.NotFound<*>)
    }

    @Test
    fun `should return NotReady error if repository is not available`() {
        whenever(folder.exists()).thenReturn(false)

        assertTrue(repository[0].left().get() is RepositoryError.NotReady)
    }

    @Test
    fun `should be able to retrieve existing elements in batch via varargs`() {
        val element = createRetrievableTestElement(0)
        val element1 = createRetrievableTestElement(1)
        val element2 = createRetrievableTestElement(2)

        val elements = repository.get(0, 2)

        assertTrue(elements.contains(element))
        assertFalse(elements.contains(element1))
        assertTrue(elements.contains(element2))
    }

    @Test
    fun `should be able to retrieve existing elements in batch via list`() {
        val element = createRetrievableTestElement(0)
        val element1 = createRetrievableTestElement(1)
        val element2 = createRetrievableTestElement(2)

        val elements = repository.get(listOf(0, 2))

        assertTrue(elements.contains(element))
        assertFalse(elements.contains(element1))
        assertTrue(elements.contains(element2))
    }

    @Test
    fun `should be able to retrieve existing elements in batch via iterator`() {
        val element = createRetrievableTestElement(0)
        val element1 = createRetrievableTestElement(1)
        val element2 = createRetrievableTestElement(2)

        val elements = repository.get(listOf(0, 2).iterator())

        assertTrue(elements.contains(element))
        assertFalse(elements.contains(element1))
        assertTrue(elements.contains(element2))
    }

    @Test
    fun `should be able to retrive all elements`() {
        val file = prepareFileForRetrieval(0)
        val file1 = prepareFileForRetrieval(1)
        val file2 = prepareFileForRetrieval(2)

        whenever(folder.files()).thenReturn(arrayOf(file, file1, file2))

        val elements = repository.all

        assertTrue(elements.contains(TestElement(0)))
        assertTrue(elements.contains(TestElement(1)))
        assertTrue(elements.contains(TestElement(2)))
    }

    @Test
    fun `should persist an element`() {
        val element = TestElement(0)

        repository.save(element)

        verify(dataHandler, times(1)).write(element)
    }

    @Test
    fun `should return NotReady error if the repository is not ready`() {
        whenever(folder.exists()).thenReturn(false)

        assertTrue(repository.save(TestElement(0)).left().get() is RepositoryError.NotReady)
    }

    @Test
    fun `should be able to persist in batch via varargs`() {
        val element = TestElement(0)
        val element1 = TestElement(1)
        whenever(dataHandler.write(element)).thenReturn(Right(element))
        whenever(dataHandler.write(element1)).thenReturn(Right(element1))

        repository.save(element, element1)

        verify(dataHandler, times(1)).write(element)
        verify(dataHandler, times(1)).write(element1)
    }

    @Test
    fun `should be able to persist in batch via collection`() {
        val element = TestElement(0)
        val element1 = TestElement(1)
        whenever(dataHandler.write(element)).thenReturn(Right(element))
        whenever(dataHandler.write(element1)).thenReturn(Right(element1))

        repository.save(listOf(element, element1))

        verify(dataHandler, times(1)).write(element)
        verify(dataHandler, times(1)).write(element1)
    }

    @Test
    fun `should be able to persist in batch via iterator`() {
        val element = TestElement(0)
        val element1 = TestElement(1)
        whenever(dataHandler.write(element)).thenReturn(Right(element))
        whenever(dataHandler.write(element1)).thenReturn(Right(element1))

        repository.save(listOf(element, element1).iterator())

        verify(dataHandler, times(1)).write(element)
        verify(dataHandler, times(1)).write(element1)
    }

    @Test
    fun `should report the repository is not available when the folder is not ready`() {
        whenever(folder.exists()).thenReturn(false)

        assertFalse(repository.isReady)
    }

    @Test
    fun `should report the repository is available when the folder is ready`() {
        whenever(folder.exists()).thenReturn(true)

        assertTrue(repository.isReady)
    }

    private fun createExistingFile(id: Int): File {
        val mockedFile = mock(File::class.java)
        whenever(mockedFile.exists()).thenReturn(true)
        doReturn(mockedFile).whenever(folder).getFile(id)
        return mockedFile
    }

    private fun createRetrievableTestElement(id: Int): TestElement {
        val mockedFile = mock(File::class.java)
        val element = TestElement(id)
        whenever(mockedFile.exists()).thenReturn(true)
        doReturn(mockedFile).whenever(folder).getFile(id)
        doReturn(Right(element)).whenever(dataHandler).read(mockedFile)
        return element
    }

    private fun prepareFileForRetrieval(id: Int): File {
        val mockedFile = mock(File::class.java)
        val element = TestElement(id)
        whenever(mockedFile.exists()).thenReturn(true)
        doReturn(mockedFile).whenever(folder).getFile(id)
        doReturn(Right(element)).whenever(dataHandler).read(mockedFile)
        return mockedFile
    }


}