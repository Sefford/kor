package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.CacheDelegate;
import com.sefford.kor.responses.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CacheInteractorTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestDelegate delegate;
    @Mock
    Response response;
    @Mock
    Error error;


    CacheInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.execute()).thenReturn(response);
        interactor = new CacheInteractor(bus, log, delegate);
    }

    @Test
    public void testOnRunSuccessful() throws Throwable {
        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, bus);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(bus, times(1)).post(response);
    }

    @Test
    public void testOnRunError() throws Throwable {
        doThrow(RuntimeException.class).when(delegate).execute();
        doReturn(error).when(delegate).composeErrorResponse((RuntimeException) any());
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, response, bus);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(response, times(0)).getSuccess();
        inOrder.verify(bus, times(0)).post(response);
        inOrder.verify(delegate, times(1)).composeErrorResponse((Exception) any());
        inOrder.verify(bus, times(1)).post(any(Error.class));
    }

    @Test
    public void testNotifyError() throws Exception {
        verifyNoMoreInteractions(bus, log, delegate);
    }

    class TestDelegate implements CacheDelegate {
        @Override
        public Response execute() {
            return null;
        }

        public Error composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public void logErrorResponse(Loggable log, Exception ex) {

        }

        @Override
        public boolean isCacheValid() {
            return false;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public void startPerformanceLog(Loggable loggable) {

        }

        @Override
        public void endPerformanceLog(Loggable loggable, long start) {

        }
    }
}