package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.example9.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult.NETWORK_FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    private static final int FAILED_RESPONSE_REPUTATION_VALUE = 0;
    private static final int SUCCESS_RESPONSE_REPUTATION_VALUE = 5;
    private FetchReputationUseCaseSync SUT;

    @Mock
    private GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchReputationUseCaseSync(mGetReputationHttpEndpointSync);
        success();
    }

    @Test
    public void fetchReputation_success_successReturned() throws Exception {
        UseCaseResult result = SUT.fetchReputation();
        Assert.assertThat(result, is(SUCCESS));
    }

    @Test
    public void fetchReputation_generalError_failureReturned() throws Exception {
        generalError();
        UseCaseResult result = SUT.fetchReputation();
        Assert.assertThat(result, is(FAILURE));
    }

    @Test
    public void fetchReputation_networkError_failureReturned() throws Exception {
        networkError();
        UseCaseResult result = SUT.fetchReputation();
        Assert.assertThat(result, is(NETWORK_FAILURE));
    }

    @Test
    public void fetchReputation_success_fetchedReputationReturned() throws Exception {
        SUT.fetchReputation();

        verify(mGetReputationHttpEndpointSync).getReputationSync();
        EndpointResult result = mGetReputationHttpEndpointSync.getReputationSync();

        Assert.assertThat(result.getReputation(), is(SUCCESS_RESPONSE_REPUTATION_VALUE));
    }

    @Test
    public void fetchReputation_generalError_zeroReputationReturned() throws Exception {
        generalError();

        SUT.fetchReputation();

        verify(mGetReputationHttpEndpointSync).getReputationSync();
        EndpointResult result = mGetReputationHttpEndpointSync.getReputationSync();

        Assert.assertThat(result.getReputation(), is(FAILED_RESPONSE_REPUTATION_VALUE));
    }

    private void success() throws Exception {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, SUCCESS_RESPONSE_REPUTATION_VALUE));
    }

    private void networkError() throws Exception {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenThrow(new NetworkErrorException());
    }

    private void generalError() throws Exception {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, FAILED_RESPONSE_REPUTATION_VALUE));
    }
}