package com.sefford.kor.interactors;

import com.sefford.kor.common.interfaces.Loggable;
import com.sefford.kor.common.interfaces.Postable;
import com.sefford.kor.errors.ErrorInterface;
import com.sefford.kor.interactors.interfaces.InteractorIdentification;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.responses.ResponseInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class NetworkInteractorTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestDelegate delegate;
    @Mock
    ErrorInterface error;

    NetworkInteractor interactor;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interactor = spy(new NetworkInteractor(bus, log, delegate) {
            @Override
            public void run() {
                // Do nathin
            }
        });
    }

    @Test
    public void testNotifyError() throws Exception {
        interactor.notifyError(error);
        verify(bus, times(1)).post(error);
    }

    class NetworkInteractorImpl extends NetworkInteractor {

        /**
         * Creates a new instance of Saving Callback
         *
         * @param bus      Notification Facility
         * @param log      Logging facilities
         * @param delegate Delegate to execute
         */
        protected NetworkInteractorImpl(Postable bus, Loggable log, NetworkDelegate delegate) {
            super(bus, log, delegate);
        }

        @Override
        public void run() {

        }
    }

    class TestDelegate implements InteractorIdentification, NetworkDelegate {

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
        public ErrorInterface composeErrorResponse(Exception error) {
            return null;
        }

        @Override
        public String getInteractorName() {
            return "";
        }
    }
}