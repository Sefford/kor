package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.interactors.delegates.NetworkDelegate;
import com.sefford.kor.usecases.components.Error;
import com.sefford.kor.usecases.components.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
public class StandardNetworkInteractorTest {

    @Mock
    Response response;
    @Mock
    TestDelegate delegate;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    Error error;

    StandardNetworkInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.execute()).thenReturn(response);
        when(delegate.postProcess(any())).thenReturn(response);
        when(delegate.composeErrorResponse(any())).thenReturn(error);
        interactor = new StandardNetworkInteractor<>(bus, log, delegate);
    }

    @Test
    public void testSuccess() throws Throwable {
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, bus);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(bus, times(1)).post(response);

    }

    @Test
    public void testSuccessWithNormalException() throws Throwable {
        doThrow(new RuntimeException()).when(delegate).saveToCache(response);
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, bus);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(bus, times(1)).post(error);

    }


    class TestDelegate implements NetworkDelegate {

        @Override
        public Response execute() {
            return null;
        }

        @Override
        public Response postProcess(Response response) {
            return null;
        }

        @Override
        public void saveToCache(Response response) {

        }

        @Override
        public Error composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public void logErrorResponse(Loggable log, Exception ex) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void startPerformanceLog(Loggable loggable) {

        }

        @Override
        public void endPerformanceLog(Loggable loggable, long start) {

        }
    }


}