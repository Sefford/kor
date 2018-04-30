package com.sefford.kor.repositories.utils

import com.sefford.kor.repositories.components.CacheFolder

import java.io.File

/**
 * Default implementation of a cache folder.
 *
 *
 * Provides a basic implementation on how a folder should provide files to the
 * [DataSource][com.sefford.kor.repositories.DiskJsonDataSource] or the [FileTimeExpirationPolicy]
 *
 * @author Saul Diaz Gonzalez <sefford@gmail.com>
 */
abstract class CacheFolderImpl<K>
/**
 * Creates a new instance of CacheFolder.
 *
 *
 * The creation of the directory will be attempted.
 *
 * @param root File pointing to the folder.
 */
(
        /**
         * File which honors the folder the instance is representing
         */
        protected val root: File) : CacheFolder<K> {

    /**
     * Creates a new instance of CacheFolder.
     *
     *
     * The creation of the directory will be attempted.
     *
     * @param root Path of the folder, it will be wrapped
     */
    constructor(root: String) : this(File(root))

    init {
        if (!this.root.exists()) {
            this.root.mkdirs()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun files(): Array<File> {
        val files = root.listFiles()
        return if (files != null) files else emptyArray()
    }

    /**
     * {@inheritDoc}
     */
    override fun exists(): Boolean {
        return root.exists()
    }
}
