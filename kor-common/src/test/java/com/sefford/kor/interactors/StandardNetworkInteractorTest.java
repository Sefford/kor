package com.sefford.kor.interactors;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.interactors.interfaces.InteractorIdentification;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.responses.ResponseInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StandardNetworkInteractorTest {

    @Mock
    ResponseInterface response;
    @Mock
    TestDelegate delegate;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    ErrorInterface error;

    StandardNetworkInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.retrieveNetworkResponse()).thenReturn(response);
        when(delegate.postProcess((ResponseInterface) any())).thenReturn(response);
        when(delegate.composeErrorResponse((Exception) any())).thenReturn(error);
        interactor = spy(new StandardNetworkInteractor(bus, log, delegate));
    }

    @Test
    public void testSuccess() throws Throwable {
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).retrieveNetworkResponse();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(interactor, times(1)).notifySuccess(response);

    }

    @Test
    public void testSuccessWithNormalException() throws Throwable {
        doThrow(new RuntimeException()).when(delegate).saveToCache(response);
        interactor.run();

        InOrder inOrder = Mockito.inOrder(delegate, interactor);
        inOrder.verify(delegate, times(1)).retrieveNetworkResponse();
        inOrder.verify(delegate, times(1)).postProcess(response);
        inOrder.verify(delegate, times(1)).saveToCache(response);
        inOrder.verify(interactor, times(1)).notifyError(error);

    }


    class TestDelegate implements InteractorIdentification, NetworkDelegate {

        @Override
        public ResponseInterface retrieveNetworkResponse() {
            return null;
        }

        @Override
        public ResponseInterface postProcess(ResponseInterface content) {
            return null;
        }

        @Override
        public void saveToCache(ResponseInterface object) {

        }

        @Override
        public ErrorInterface composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public String getInteractorName() {
            return null;
        }
    }


}