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

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.sefford.common.interfaces.Loggable
import com.sefford.kor.repositories.interfaces.CacheFolder
import com.sefford.kor.repositories.interfaces.RepoElement
import com.sefford.kor.repositories.interfaces.Repository

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

/**
 * Repository for saving JSon directly to disk
 *
 * @author Saul Diaz <sefford></sefford>@gmail.com>
 */
open class DiskJsonDataSource<K, V : RepoElement<K>>
/**
 * Created a new DiskJsonDataSource
 *
 * @param folder Root folder of the cache
 * @param gson   Gson Converter
 * @param log    Loggable for I/O Errors
 * @param clazz  Class of the Repository elements to allow the conversion between JSon and POJOs and viceversa
 */
(
        /**
         * Folder where the IDs will be saved
         */
        internal val folder: CacheFolder<K>,
        /**
         * Gson Converter
         */
        internal val gson: Gson,
        /**
         * Loggable for I/O Errors
         */
        internal val log: Loggable,
        /**
         * Class of the Repository elements to allow the conversion between JSon and POJOs and viceversa
         */
        internal val clazz: Class<V>) : Repository<K, V> {

    override val all: Collection<V>
        get() {
            val elements = ArrayList<V>()
            val files = folder.files()
            for (i in files.indices) {
                val element = read(files[i])
                if (element != null) {
                    elements.add(element)
                }
            }
            return elements
        }

    override val isAvailable: Boolean
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

    override fun delete(id: K, element: V?) {
        delete(id)
    }

    override fun delete(id: K) {
        val file = folder.getFile(id)
        file?.delete()
    }

    override fun deleteAll(elements: Collection<V>) {
        for (element in elements) {
            delete(element.id, element)
        }
    }

    override fun get(id: K): V? {
        return read(folder.getFile(id))
    }

    override fun getAll(ids: Collection<K>): Collection<V> {
        val elements = ArrayList<V>()
        for (id in ids) {
            val element = get(id)
            if (element != null) {
                elements.add(element)
            }
        }
        return elements
    }

    override fun save(element: V): V {
        write(element)
        return element
    }

    override fun saveAll(elements: Collection<V>): Collection<V> {
        for (element in elements) {
            save(element)
        }
        return elements
    }

    open fun write(element: V) {
        try {
            val file = folder.getFile(element.id)
            if (file != null && !file.exists()) {
                file.createNewFile()
            }
            val outputStreamWriter = FileOutputStream(file)
            outputStreamWriter.write(gson.toJson(element).toByteArray())
            outputStreamWriter.close()
        } catch (e: IOException) {
            log.e(TAG, "File write failed: " + e.toString(), e)
        } catch (e: OutOfMemoryError) {
            log.e(TAG, "File write failed: " + e.toString(), e)
        } catch (e: IncompatibleClassChangeError) {
            log.e(TAG, "File write failed: " + e.toString(), e)
        }

    }

    open fun read(file: File?): V? {
        if (file != null && file.exists()) {
            try {
                val length = file.length().toInt()
                val bytes = ByteArray(length)

                log.d(TAG, "File length:" + length)

                val `in` = FileInputStream(file)
                try {
                    `in`.read(bytes)
                } finally {
                    `in`.close()
                }

                return gson.fromJson(String(bytes), clazz)
            } catch (e: IOException) {
                log.e(TAG, "File read failed: " + e.toString(), e)
            } catch (e: OutOfMemoryError) {
                log.e(TAG, "File read failed: " + e.toString(), e)
            } catch (e: UnsupportedOperationException) {
                file.delete()
            } catch (e: IncompatibleClassChangeError) {
                file.delete()
            } catch (e: IllegalArgumentException) {
                file.delete()
            } catch (e: JsonParseException) {
                file.delete()
            }

        }
        return null
    }

    companion object {

        private val TAG = "DiskJsonDataSource"
    }
}
