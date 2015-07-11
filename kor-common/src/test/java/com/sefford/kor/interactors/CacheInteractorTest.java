package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.CacheDelegate;
import com.sefford.kor.responses.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
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
        interactor = spy(new CacheInteractor(bus, log, delegate));
    }

    @Test
    public void testOnRunSuccessful() throws Throwable {
        when(response.isSuccess()).thenReturn(true);

        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, response, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(interactor, times(1)).notifySuccess(response);
    }

    @Test
    public void testOnRunFailure() throws Throwable {
        when(response.isSuccess()).thenReturn(false);

        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, response, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(interactor, times(0)).notifySuccess(response);
        inOrder.verify(interactor, times(0)).notifyError(Matchers.<Error>any());
    }

    @Test
    public void testOnRunError() throws Throwable {
        when(delegate.execute()).thenThrow(Exception.class);

        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, response, interactor);
        inOrder.verify(delegate, times(1)).execute();
        inOrder.verify(response, times(0)).isSuccess();
        inOrder.verify(interactor, times(0)).notifySuccess(response);
        inOrder.verify(delegate, times(1)).composeErrorResponse((Exception) any());
        inOrder.verify(interactor, times(1)).notifyError((Error) any());
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
        public boolean isCacheValid() {
            return false;
        }

        @Override
        public String getInteractorName() {
            return "";
        }
    }
}