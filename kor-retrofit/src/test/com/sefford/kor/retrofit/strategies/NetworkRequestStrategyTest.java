package com.sefford.kor.retrofit.strategies;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.BaseError;
import com.sefford.kor.requests.interfaces.NetworkRequest;
import com.sefford.kor.requests.interfaces.RequestIdentification;
import com.sefford.kor.responses.BaseResponse;
import com.sefford.kor.retrofit.interfaces.RetrofitRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit.RetrofitError;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NetworkRequestStrategyTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestRequest request;
    @Mock
    BaseError error;
    @Mock
    RetrofitError retrofitError;

    NetworkRequestStrategy executor;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(request.composeErrorResponse(retrofitError)).thenReturn(error);
        executor = spy(new NetworkRequestStrategy(bus, log, request) {
            @Override
            public void onRun() throws Throwable {
                // Do nathin
            }
        });
    }

    @Test
    public void testNotifyError() throws Exception {
        executor.notifyError(error);
        verify(bus, times(1)).post(error);
    }

    class NetworkRequestStrategyImpl extends NetworkRequestStrategy {

        /**
         * Creates a new instance of Saving Callback
         *
         * @param bus     Notification Facility
         * @param log     Logging facilities
         * @param request Request to execute
         */
        protected NetworkRequestStrategyImpl(Postable bus, Loggable log, NetworkRequest request) {
            super(bus, log, request);
        }

        @Override
        public void onRun() throws Throwable {

        }
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