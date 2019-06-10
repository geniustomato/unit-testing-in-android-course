package com.techyourchance.testdrivendevelopment.exercise7.networking;


import com.techyourchance.testdrivendevelopment.example9.networking.NetworkErrorException;

public interface GetReputationHttpEndpointSync {

    enum EndpointStatus {
        SUCCESS,
        GENERAL_ERROR,
        NETWORK_ERROR
    }

    class EndpointResult {
        private final EndpointStatus mEndpointStatus;
        private final int mReputation;

        public EndpointResult(EndpointStatus endpointStatus, int reputation) {
            mEndpointStatus = endpointStatus;
            mReputation = reputation;
        }

        public EndpointStatus getStatus() {
            return mEndpointStatus;
        }

        public int getReputation() {
            return mReputation;
        }
    }

    EndpointResult getReputationSync() throws NetworkErrorException;

}
