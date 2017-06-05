package com.sefford.kor.repositories;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sefford.common.interfaces.Loggable;
import com.sefford.kor.repositories.interfaces.RepoElement;
import com.sefford.kor.repositories.interfaces.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sefford on 6/5/17.
 */
public class DiskJsonRepository<K, V extends RepoElement<K>>
        implements Repository<K, V> {

    private static final String TAG = "DiskJsonRepository";

    final Gson gson;
    final Loggable log;
    final File folder;
    final Class<V> clazz;

    public DiskJsonRepository(File folder, Gson gson, Loggable log, Class<V> clazz) {
        this.gson = gson;
        this.log = log;
        this.folder = folder;
        this.clazz = clazz;
        if (folder != null && !this.folder.exists()) {
            this.folder.mkdirs();
        }
    }

    @Override
    public void clear() {
        final File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    @Override
    public boolean contains(K id) {
        final File file = getFile(id);
        return file != null && file.exists();
    }

    @Override
    public void delete(K id, V element) {
        final File file = getFile(id);
        if (file != null) {
            file.delete();
        }
    }

    @Override
    public void deleteAll(List<V> elements) {
        for (int i = 0; i < elements.size(); i++) {
            delete(elements.get(i).getId(), elements.get(i));
        }
    }

    @Override
    public V get(K id) {
        return read(getFile(id));
    }

    @Override
    public Collection<V> getAll(Collection<K> ids) {
        final List<V> elements = new ArrayList<>();
        for (K id : ids) {
            final V element = get(id);
            if (element != null) {
                elements.add(element);
            }
        }
        return elements;
    }

    @Override
    public Collection<V> getAll() {
        final List<V> elements = new ArrayList<>();
        final File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                final V element = read(files[i]);
                if (element != null) {
                    elements.add(element);
                }
            }
        }
        return elements;
    }

    @Override
    public V save(V element) {
        write(element);
        return element;
    }

    @Override
    public Collection<V> saveAll(Collection<V> elements) {
        for (V element : elements) {
            save(element);
        }
        return elements;
    }

    @Override
    public boolean isAvailable() {
        return folder != null && folder.exists();
    }

    File getFile(K id) {
        return new File(folder, id + ".json");
    }

    void write(V element) {
        try {
            final File file = getFile(element.getId());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStreamWriter = new FileOutputStream(file);
            outputStreamWriter.write(gson.toJson(element).getBytes());
            outputStreamWriter.close();
        } catch (IOException | OutOfMemoryError | IncompatibleClassChangeError e) {
            log.e(TAG, "File write failed: " + e.toString(), e);
        }
    }

    V read(File file) {
        if (file != null && file.exists()) {
            try {
                final int length = (int) file.length();
                final byte[] bytes = new byte[length];

                log.d(TAG, "File length:" + length);

                FileInputStream in = new FileInputStream(file);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }

                return gson.fromJson(new String(bytes), clazz);
            } catch (IOException | OutOfMemoryError e) {
                log.e(TAG, "File read failed: " + e.toString(), e);
            } catch (UnsupportedOperationException | IncompatibleClassChangeError | IllegalArgumentException | JsonParseException e) {
                file.delete();
            }
        }
        return null;
    }
}
