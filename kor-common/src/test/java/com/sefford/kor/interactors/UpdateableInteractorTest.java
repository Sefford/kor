package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.UpdateableDelegate;
import com.sefford.kor.responses.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdateableInteractorTest {

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

    UpdateableInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.keepLooping()).thenReturn(Boolean.TRUE)
                .thenReturn(Boolean.TRUE)
                .thenReturn(Boolean.FALSE);
        when(delegate.execute()).thenReturn(response);
        when(delegate.postProcess(response)).thenReturn(response);
        when(delegate.execute()).thenReturn(response);
        when(delegate.composeErrorResponse((Exception) any())).thenReturn(error);

        interactor = spy(new UpdateableInteractor(bus, log, delegate));
    }

    @Test
    public void testRunTwoLoops() throws Exception {
        interactor.run();

        verify(delegate, times(3)).keepLooping();
        verify(delegate, times(2)).execute();
        verify(delegate, times(2)).postProcess(response);
        verify(delegate, times(2)).saveToCache(response);
        verify(interactor, times(2)).notifySuccess(response);
    }

    @Test
    public void testRunErrorSituation() throws Exception {
        when(delegate.execute()).thenThrow(Exception.class);

        interactor.run();

        InOrder order = inOrder(delegate, interactor);
        order.verify(delegate, times(1)).keepLooping();
        order.verify(delegate, times(1)).execute();
        order.verify(delegate, times(0)).postProcess(response);
        order.verify(delegate, times(0)).saveToCache(response);
        order.verify(delegate, times(1)).composeErrorResponse((Exception) any());
        order.verify(interactor, times(1)).notifyError(error);
    }

    class TestDelegate implements UpdateableDelegate {

        @Override
        public boolean keepLooping() {
            return false;
        }

        @Override
        public Response execute() throws Exception {
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
        public String getInteractorName() {
            return null;
        }
    }

}