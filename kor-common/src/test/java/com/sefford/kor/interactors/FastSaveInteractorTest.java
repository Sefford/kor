package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.FastDelegate;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.responses.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FastSaveInteractorTest {

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

    FastNetworkInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.execute()).thenReturn(response);
        when(delegate.postProcess((Response) any())).thenReturn(response);
        when(delegate.fastSave((Response) any())).thenReturn(response);
        when(delegate.composeErrorResponse((Exception) any())).thenReturn(error);
        interactor = spy(new FastNetworkInteractor(bus, log, delegate));
    }

    @Test
    public void testSuccess() throws Throwable {
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).fastSave(response);
        inOrder.verify(interactor, times(1)).notify(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);

    }

    @Test
    public void testSuccessWithExceptionBeforeSaveToCache() throws Throwable {
        doThrow(new RuntimeException()).when(delegate).fastSave(response);
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).fastSave(response);
        inOrder.verify(interactor, times(0)).notify(response);
        inOrder.verify(delegate, times(0)).saveToCache(response);
        inOrder.verify(interactor, times(1)).notify(error);

    }

    @Test
    public void testSuccessWithExceptionOnSaveToCache() throws Throwable {
        doThrow(new RuntimeException()).when(delegate).saveToCache(response);
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).fastSave(response);
        inOrder.verify(interactor, times(1)).notify(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(interactor, times(0)).notify(error);

    }

    class TestDelegate implements NetworkDelegate, FastDelegate {

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
        public void logErrorResponse(Loggable log, Exception x) {

        }

        @Override
        public Response fastSave(Response response) {
            return response;
        }

        @Override
        public String getInteractorName() {
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