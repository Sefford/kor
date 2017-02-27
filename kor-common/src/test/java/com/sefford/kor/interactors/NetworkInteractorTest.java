package com.sefford.kor.interactors;

import com.sefford.common.interfaces.Loggable;
import com.sefford.common.interfaces.Postable;
import com.sefford.kor.errors.Error;
import com.sefford.kor.interactors.interfaces.NetworkDelegate;
import com.sefford.kor.responses.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class NetworkInteractorTest {

    @Mock
    Postable bus;
    @Mock
    Loggable log;
    @Mock
    TestDelegate delegate;
    @Mock
    Error error;

    NetworkInteractor interactor;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        interactor = spy(new NetworkInteractor(bus, log, delegate) {
            @Override
            public void run() {
                // Do nathin
            }

            @Override
            public Object execute() {
                return null;
            }
        });
    }

    @Test
    public void testNotifyError() throws Exception {
        interactor.notify(error);
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

        @Override
        public Object execute() {
            return null;
        }
    }

    class TestDelegate implements NetworkDelegate {

        @Override
        public Response execute() {
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
        public void logErrorResponse(Loggable log) {

        }

        @Override
        public String getInteractorName() {
            return "";
        }
    }
}