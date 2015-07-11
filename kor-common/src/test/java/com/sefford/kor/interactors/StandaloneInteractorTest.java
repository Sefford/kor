package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sefford on 5/03/15.
 */
public class StandaloneInteractorTest {
    @Mock
    Interactor mockedInteractor;
    @Mock
    Postable bus;
    @Mock
    Object options;
    @Mock
    Loggable log;
    @Mock
    ThreadPoolExecutor threadPoolExecutor;

    InteractorTest interactor;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interactor = spy(new InteractorTest(log, threadPoolExecutor));
    }

    @Test
    public void testExecute() throws Exception {
        interactor.execute(bus, options);

        verify(interactor, times(1)).instantiateInteractor(bus, options);
    }

    public class InteractorTest extends StandaloneInteractor<Object> {

        protected InteractorTest(Loggable log, ThreadPoolExecutor executor) {
            super(log, executor);
        }

        @Override
        protected Interactor instantiateInteractor(Postable bus, Object options) {
            return mockedInteractor;
        }
    }
}