package com.sefford.kor.interactors;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.interactors.interfaces.CacheDelegate;
import com.sefford.kor.interactors.interfaces.InteractorIdentification;
import com.sefford.kor.responses.ResponseInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CacheInteractorTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestDelegate delegate;
    @Mock
    ResponseInterface response;


    CacheInteractor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(delegate.retrieveFromCache()).thenReturn(response);
        interactor = spy(new CacheInteractor(bus, log, delegate));
    }

    @Test
    public void testOnRunSuccessful() throws Throwable {
        when(response.isSuccess()).thenReturn(true);

        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, response, interactor);
        inOrder.verify(delegate, times(1)).retrieveFromCache();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(interactor, times(1)).notifySuccess(response);
    }

    @Test
    public void testOnRunFailure() throws Throwable {
        when(response.isSuccess()).thenReturn(false);

        interactor.run();
        InOrder inOrder = Mockito.inOrder(delegate, response, interactor);
        inOrder.verify(delegate, times(1)).retrieveFromCache();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(interactor, times(0)).notifySuccess(response);
        inOrder.verify(interactor, times(0)).notifyError(Matchers.<ErrorInterface>any());
    }


    @Test
    public void testNotifyError() throws Exception {
        verifyNoMoreInteractions(bus, log, delegate);
    }

    class TestDelegate implements InteractorIdentification, CacheDelegate {
        @Override
        public ResponseInterface retrieveFromCache() {
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