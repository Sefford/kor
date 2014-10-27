package com.sefford.kor.retrofit.strategies;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.requests.interfaces.RequestIdentification;
import com.sefford.kor.responses.ResponseInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RequestStrategyTest {
    @Mock
    ResponseInterface response;
    @Mock
    RequestIdentification request;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    ErrorInterface error;
    private RequestStrategy executor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        executor = spy(new RequestStrategy(bus, log, request) {
            @Override
            public void onRun() throws Throwable {
                // This space for rent
            }

            @Override
            public void notifyError(ErrorInterface error) {
                // This one too
            }
        });
    }

    @Test
    public void testNotifySuccess() throws Exception {
        executor.notifySuccess(response);
        verify(bus, times(1)).post(response);
    }

    @Test
    public void testOnAdded() throws Exception {
        executor.onAdded();
        verify(request, times(1)).getRequestName();
    }

    @Test
    public void testOnCancel() throws Exception {
        executor.onCancel();
        verify(request, times(1)).getRequestName();
    }

    @Test
    public void testShouldReRunOnThrowable() throws Exception {
        final Throwable mock = mock(Throwable.class);
        assertThat(executor.shouldReRunOnThrowable(mock), equalTo(Boolean.FALSE));
        verify(log, times(1)).e(RequestStrategy.TAG, mock.getMessage(), mock);
    }

    @Test
    public void testGetRequest() throws Exception {
        assertThat(executor.getRequest(), equalTo(request));
    }
}