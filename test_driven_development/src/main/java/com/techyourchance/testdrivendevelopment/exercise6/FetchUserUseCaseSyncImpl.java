package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private final UsersCache mUsersCache;
    private final FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;

    public FetchUserUseCaseSyncImpl(UsersCache usersCache, FetchUserHttpEndpointSync fetchUserHttpEndpointSync) {
        mUsersCache = usersCache;
        mFetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        FetchUserHttpEndpointSync.EndpointResult result;

        // Check cache
        User cachedUser = mUsersCache.getUser(userId);

        if (cachedUser == null) {
            try {
                result = mFetchUserHttpEndpointSync.fetchUserSync(userId);
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }

            switch (result.getStatus()) {
                case SUCCESS: {
                    User newUser = new User(result.getUserId(), result.getUsername());
                    mUsersCache.cacheUser(newUser);

                    return new UseCaseResult(Status.SUCCESS, newUser);
                }
                case AUTH_ERROR:
                case GENERAL_ERROR:
                    return new UseCaseResult(Status.FAILURE, null);
                default:
                    throw new RuntimeException();
            }
        } else {
            return new UseCaseResult(Status.SUCCESS, cachedUser);
        }
    }
}
