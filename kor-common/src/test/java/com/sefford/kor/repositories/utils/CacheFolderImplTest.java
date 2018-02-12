package com.sefford.kor.repositories.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sefford on 12/13/17.
 */
public class CacheFolderImplTest {

    CacheFolderImpl folder;

    @Mock
    File file;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        folder = new CacheFolderTestImpl(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void crashesIfFolderNull() throws Exception {
        new CacheFolderTestImpl(null);
    }

    @Test
    public void retrievesFiles() throws Exception {
        File file = givenAListOfFiles();

        final File[] files = folder.files();

        assertThat(files.length, is(1));
        assertThat(files[0], is(file));
    }

    @Test
    public void exists() throws Exception {
        when(file.exists()).thenReturn(Boolean.TRUE);

        assertThat(file.exists(), is(Boolean.TRUE));
    }

    private File givenAListOfFiles() {
        final File mockedFile = mock(File.class);
        when(file.listFiles()).thenReturn(new File[]{mockedFile});
        return mockedFile;
    }

    class CacheFolderTestImpl extends CacheFolderImpl<String> {

        public CacheFolderTestImpl(File root) {
            super(root);
        }

        @Override
        public File getFile(String id) {
            return null;
        }
    }

}