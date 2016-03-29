package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.Delegate;
import com.sefford.kor.responses.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class InteractorTest {
    @Mock
    Response response;
    @Mock
    Delegate delegate;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    Error error;

    Interactor interactor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interactor = spy(new Interactor(bus, log, delegate) {
            @Override
            public void run() {
                // This space for rent
            }

            @Override
            public Object execute() {
                return response;
            }

        });
    }

    @Test
    public void testNotifySuccess() throws Exception {
        interactor.notify(response);
        verify(bus, times(1)).post(response);
    }

    @Test
    public void testGetDelegate() throws Exception {
        assertThat(interactor.getDelegate(), equalTo(delegate));
    }
}