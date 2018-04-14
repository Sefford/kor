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

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import com.chicisimo.data.StubDataSource
import com.sefford.common.interfaces.Loggable
import com.sefford.kor.interactors.RepositoryError
import com.sefford.kor.repositories.interfaces.CacheFolder
import com.sefford.kor.repositories.interfaces.JsonConverter
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Repository for saving JSon directly to disk
 *
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
class DiskJsonDataSource<K, V : RepoElement<K>>
/**
 * Created a new DiskJsonDataSource
 *
 * @param folder Root folder of the cache
 * @param converter   Gson Converter
 * @param log    Loggable for I/O Errors
 * @param clazz  Class of the Repository elements to allow the conversion between JSon and POJOs and viceversa
 */
internal constructor(
        /**
         * Folder where the IDs will be saved
         */
        private val folder: CacheFolder<K>,

        private val data: DataHandler<K, V>) : Repository<K, V>, StubDataSource<K, V> {

    constructor(folder: CacheFolder<K>, converter: JsonConverter<V>, log: Loggable) : this(folder, DefaultDataHandler(folder, converter, log))

    override val all: Collection<V>
        get() {
            val elements = ArrayList<V>()
            val files = folder.files()
            for (i in files.indices) {
                data.read(files[i]).fold({ files[i].delete() }, { elements.add(it) })
            }
            return elements
        }

    override val isReady: Boolean
        get() = folder.exists()

    override fun clear() {
        val files = folder.files()
        if (true) {
            for (i in files.indices) {
                files[i].delete()
            }
        }
    }

    override fun contains(id: K): Boolean {
        val file = folder.getFile(id)
        return file?.exists() ?: false
    }

    override fun delete(id: K, element: V) {
        delete(id)
    }

    override fun delete(id: K) {
        val file = folder.getFile(id)
        file?.delete()
    }

    override fun get(id: K): Either<RepositoryError, V> {
        if (!isReady) {
            return Left(RepositoryError.NotReady)
        }
        val file = folder.getFile(id)
        if (file == null || !file.exists()) {
            return Left(RepositoryError.NotFound(id))
        }
        return data.read(file)
    }

    override fun save(element: V): Either<RepositoryError, V> {
        if (!isReady) {
            return Left(RepositoryError.NotReady)
        }
        return data.write(element)
    }

    internal interface DataHandler<K, V> {
        fun read(file: File): Either<RepositoryError, V>

        fun write(element: V): Either<RepositoryError, V>
    }

    class DefaultDataHandler<K, V : RepoElement<K>>(val folder: CacheFolder<K>, val converter: JsonConverter<V>, val log: Loggable) : DataHandler<K, V> {
        override fun read(file: File): Either<RepositoryError, V> {
            return try {
                val length = file.length().toInt()
                val bytes = ByteArray(length)

                log.d(TAG, "File length:" + length)

                val `in` = FileInputStream(file)
                try {
                    `in`.read(bytes)
                } finally {
                    `in`.close()
                }
                return converter.deserialize(String(bytes))
            } catch (e: IOException) {
                log.e(TAG, "File read failed: " + e.toString(), e)
                Left(RepositoryError.CannotRetrieve(e))
            } catch (e: OutOfMemoryError) {
                log.e(TAG, "File read failed: " + e.toString(), e)
                Left(RepositoryError.CannotRetrieve(e))
            } catch (e: UnsupportedOperationException) {
                file.delete()
                Left(RepositoryError.CannotRetrieve(e))
            } catch (e: IncompatibleClassChangeError) {
                file.delete()
                Left(RepositoryError.CannotRetrieve(e))
            } catch (e: IllegalArgumentException) {
                file.delete()
                Left(RepositoryError.CannotRetrieve(e))
            }
        }

        override fun write(element: V): Either<RepositoryError, V> {
            return try {
                val file = folder.getFile(element.id)
                if (file != null && !file.exists()) {
                    file.createNewFile()
                }
                val outputStreamWriter = FileOutputStream(file)
                return converter.serialize(element).fold({ Left(it) },
                        {
                            outputStreamWriter.write(it.toByteArray())
                            outputStreamWriter.close()
                            Right(element)
                        })
            } catch (e: IOException) {
                Left(RepositoryError.CannotPersist(e))
            } catch (e: OutOfMemoryError) {
                Left(RepositoryError.CannotPersist(e))
            } catch (e: IncompatibleClassChangeError) {
                Left(RepositoryError.CannotPersist(e))
            }
        }
    }

    companion object {

        private val TAG = "DiskJsonDataSource"
    }
}