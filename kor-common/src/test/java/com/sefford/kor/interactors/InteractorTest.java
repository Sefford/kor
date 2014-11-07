package com.sefford.kor.interactors;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.interactors.interfaces.InteractorIdentification;
import com.sefford.kor.responses.ResponseInterface;

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
    ResponseInterface response;
    @Mock
    InteractorIdentification delegate;
    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    ErrorInterface error;

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
            public void notifyError(ErrorInterface error) {
                // This one too
            }
        });
    }

    @Test
    public void testNotifySuccess() throws Exception {
        interactor.notifySuccess(response);
        verify(bus, times(1)).post(response);
    }

    @Test
    public void testGetDelegate() throws Exception {
        assertThat(interactor.getDelegate(), equalTo(delegate));
    }
}