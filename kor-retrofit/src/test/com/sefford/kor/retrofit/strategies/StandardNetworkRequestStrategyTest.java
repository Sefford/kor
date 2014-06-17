package com.sefford.kor.retrofit.strategies;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.BaseError;
import com.sefford.kor.requests.interfaces.RequestIdentification;
import com.sefford.kor.responses.BaseResponse;
import com.sefford.kor.retrofit.interfaces.RetrofitRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import retrofit.RetrofitError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StandardNetworkRequestStrategyTest {

    @Mock
    BaseResponse response;
    @Mock
    TestRequest request;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    BaseError error;

    StandardNetworkRequestStrategy executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(request.retrieveNetworkResponse()).thenReturn(response);
        when(request.postProcess((BaseResponse) any())).thenReturn(response);
        when(request.composeErrorResponse((Exception) any())).thenReturn(error);
        executor = spy(new StandardNetworkRequestStrategy(bus, log, request));
    }

    @Test
    public void testSuccess() throws Throwable {
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(1)).postProcess(response);
        inOrder.verify(request, times(1)).saveToCache(response);
        inOrder.verify(executor, times(1)).notifySuccess(response);

    }

    @Test
    public void testSuccessWithException() throws Throwable {
        doThrow(new RuntimeException()).when(request).saveToCache(response);
        executor.onRun();

        InOrder inOrder = Mockito.inOrder(request, executor);
        inOrder.verify(request, times(1)).retrieveNetworkResponse();
        inOrder.verify(request, times(1)).postProcess(response);
        inOrder.verify(request, times(1)).saveToCache(response);
        inOrder.verify(executor, times(1)).notifyError(error);

    }

    class TestRequest implements RequestIdentification, RetrofitRequest {

        @Override
        public BaseResponse retrieveNetworkResponse() {
            return null;
        }

        @Override
        public BaseResponse postProcess(BaseResponse content) {
            return null;
        }

        @Override
        public void saveToCache(BaseResponse object) {

        }

        @Override
        public BaseError composeErrorResponse(RetrofitError error) {
            return null;
        }

        @Override
        public BaseError composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public String getRequestName() {
            return null;
        }
    }


}