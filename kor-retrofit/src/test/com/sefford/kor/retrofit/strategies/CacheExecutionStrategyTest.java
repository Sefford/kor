package com.sefford.kor.retrofit.strategies;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.requests.interfaces.CacheRequest;
import com.sefford.kor.requests.interfaces.RequestIdentification;
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

public class CacheExecutionStrategyTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestRequest request;
    @Mock
    ResponseInterface response;


    CacheExecutionStrategy executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(request.retrieveFromCache()).thenReturn(response);
        executor = spy(new CacheExecutionStrategy(bus, log, request));
    }

    @Test
    public void testOnRunSuccessful() throws Throwable {
        when(response.isSuccess()).thenReturn(true);

        executor.onRun();
        InOrder inOrder = Mockito.inOrder(request, response, executor);
        inOrder.verify(request, times(1)).retrieveFromCache();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(executor, times(1)).notifySuccess(response);
    }

    @Test
    public void testOnRunFailure() throws Throwable {
        when(response.isSuccess()).thenReturn(false);

        executor.onRun();
        InOrder inOrder = Mockito.inOrder(request, response, executor);
        inOrder.verify(request, times(1)).retrieveFromCache();
        inOrder.verify(response, times(1)).isSuccess();
        inOrder.verify(executor, times(0)).notifySuccess(response);
        inOrder.verify(executor, times(0)).notifyError(Matchers.<ErrorInterface>any());
    }


    @Test
    public void testNotifyError() throws Exception {
        verifyNoMoreInteractions(bus, log, request);
    }

    class TestRequest implements RequestIdentification, CacheRequest {
        @Override
        public ResponseInterface retrieveFromCache() {
            return null;
        }

        @Override
        public boolean isCacheValid() {
            return false;
        }

        @Override
        public String getRequestName() {
            return null;
        }
    }
}