package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.example9.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

public class FetchReputationUseCaseSync {

    private GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        mGetReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }


    public UseCaseResult fetchReputation() {
        EndpointResult result;

        try {
            result = mGetReputationHttpEndpointSync.getReputationSync();
        } catch (NetworkErrorException e) {
            return UseCaseResult.NETWORK_FAILURE;
        }

        switch (result.getStatus()) {
            case SUCCESS:
                return UseCaseResult.SUCCESS;
            case GENERAL_ERROR:
                return UseCaseResult.FAILURE;
            default:
                throw new RuntimeException("Invalid result: " + result);
        }
    }

    public enum UseCaseResult {
        FAILURE, SUCCESS, NETWORK_FAILURE
    }
}
