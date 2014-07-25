package com.sefford.kor.retrofit.strategies;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.requests.interfaces.FastSaving;
import com.sefford.kor.requests.interfaces.RequestIdentification;
import com.sefford.kor.responses.ResponseInterface;
import com.sefford.kor.retrofit.interfaces.RetrofitRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import retrofit.RetrofitError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FastSaveNetworkRequestStrategyTest {

    @Mock
    ResponseInterface response;
    @Mock
    TestRequest request;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    ErrorInterface error;

    FastSaveNetworkRequestStrategy executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(request.retrieveNetworkResponse()).thenReturn(response);
        when(request.postProcess((ResponseInterface) any())).thenReturn(response);
        when(request.fastSave((ResponseInterface) any())).thenReturn(response);
        when(request.composeErrorResponse((Exception) any())).thenReturn(error);
        when(request.composeErrorResponse((RetrofitError) any())).thenReturn(error);
        executor = spy(new FastSaveNetworkRequestStrategy(bus, log, request));
    }

    @Test
    public void testSuccess() throws Throwable {
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(1)).postProcess(response);
        inOrder.verify(request, times(1)).fastSave(response);
        inOrder.verify(executor, times(1)).notifySuccess(response);
        inOrder.verify(request, times(1)).saveToCache(response);

    }

    @Test
    public void testSuccessWithExceptionBeforeSaveToCache() throws Throwable {
        doThrow(new RuntimeException()).when(request).fastSave(response);
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(1)).postProcess(response);
        inOrder.verify(request, times(1)).fastSave(response);
        inOrder.verify(executor, times(0)).notifySuccess(response);
        inOrder.verify(request, times(0)).saveToCache(response);
        inOrder.verify(executor, times(1)).notifyError(error);

    }

    @Test
    public void testSuccessWithExceptionOnSaveToCache() throws Throwable {
        doThrow(new RuntimeException()).when(request).saveToCache(response);
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(1)).postProcess(response);
        inOrder.verify(request, times(1)).fastSave(response);
        inOrder.verify(executor, times(1)).notifySuccess(response);
        inOrder.verify(request, times(1)).saveToCache(response);
        inOrder.verify(executor, times(0)).notifyError(error);

    }

    @Test
    public void testSuccessWithRetrofitException() throws Throwable {
        RetrofitError retrofitError = mock(RetrofitError.class);
        doThrow(retrofitError).when(request).retrieveNetworkResponse();
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(0)).postProcess(response);
        inOrder.verify(request, times(0)).saveToCache(response);
        inOrder.verify(request, times(1)).composeErrorResponse(retrofitError);
        inOrder.verify(executor, times(1)).notifyError(error);
    }

    class TestRequest implements RequestIdentification, RetrofitRequest, FastSaving {

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
        public ErrorInterface composeErrorResponse(RetrofitError error) {
            return null;
        }

        @Override
        public ErrorInterface composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public String getRequestName() {
            return null;
        }

        @Override
        public ResponseInterface fastSave(ResponseInterface response) {
            return null;
        }
    }
}