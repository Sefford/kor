package com.sefford.kor.repositories

import com.sefford.kor.utils.FakeConverter
import org.junit.Before

class MemoryJsonDataSourceTest : RepositoryTestSuite() {

    @Before
    fun setUp() {
        repository = MemoryJsonDataSource(FakeConverter())
    }
}