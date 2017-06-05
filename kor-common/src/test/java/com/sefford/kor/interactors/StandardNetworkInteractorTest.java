package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
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
        when(delegate.postProcess((Response) any())).thenReturn(response);
        when(delegate.composeErrorResponse((Exception) any())).thenReturn(error);
        interactor = spy(new StandardNetworkInteractor(bus, log, delegate));
    }

    @Test
    public void testSuccess() throws Throwable {
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(interactor, times(1)).notify(response);

    }

    @Test
    public void testSuccessWithNormalException() throws Throwable {
        doThrow(new RuntimeException()).when(delegate).saveToCache(response);
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(interactor, times(1)).notify(error);

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
        public String getInteractorName() {
            return null;
        }
    }


}