package com.sefford.kor.repositories.utils;

import com.sefford.kor.repositories.interfaces.CacheFolder;

import java.io.File;

/**
 * Default implementation of a cache folder.
 * <p>
 * Provides a basic implementation on how a folder should provide files to the {@link com.sefford.kor.repositories.DiskJsonDataSource DataSource}
 * or the {@link FileTimeExpirationPolicy}
 *
 * @author Saul Diaz Gonzalez <sefford@gmail.com>
 */
public abstract class CacheFolderImpl<K> implements CacheFolder<K> {

    /**
     * File which honors the folder the instance is representing
     */
    protected final File root;

    /**
     * Creates a new instance of CacheFolder.
     * <p>
     * The creation of the directory will be attempted.
     *
     * @param root Path of the folder, it will be wrapped
     */
    public CacheFolderImpl(String root) {
        this(new File(root));
    }

    /**
     * Creates a new instance of CacheFolder.
     * <p>
     * The creation of the directory will be attempted.
     *
     * @param root File pointing to the folder.
     */
    public CacheFolderImpl(File root) {
        if (root == null) {
            throw new NullPointerException("root cannot be null");
        }
        this.root = root;
        if (root != null && !this.root.exists()) {
            this.root.mkdirs();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] files() {
        return root.listFiles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        return root != null && root.exists();
    }
}