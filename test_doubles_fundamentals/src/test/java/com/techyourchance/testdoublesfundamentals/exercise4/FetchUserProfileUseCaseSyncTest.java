package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "123456";
    public static final String FULL_NAME = "Carlo Trajano";
    public static final String IMAGE_URL = "imageUrl";

    private FetchUserProfileUseCaseSync SUT;
    public UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSync;
    public UsersCacheTd mUsersCache;

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSync = new UserProfileHttpEndpointSyncTd();
        mUsersCache = new UsersCacheTd();

        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSync, mUsersCache);
    }

    // Get user profile success

    @Test
    public void userSync_successUserProfileRetrievedAndCached_userProfileReturned() {
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));

        SUT.fetchUserProfileSync(USER_ID);

        Assert.assertThat(mUserProfileHttpEndpointSync.mUserId, is(USER_ID));
        Assert.assertThat(mUsersCache.getUser(USER_ID), notNullValue());
    }


    // Get user profile failure

    // If get user success, user is cached user

    //

    // Helper Classes
    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId;

        public boolean isAuthError = false;
        public boolean isGeneralError = false;
        public boolean isServerError = false;
        private boolean isNetworkException = false;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;

            if (isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (isNetworkException) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, mUserId, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {
        private ArrayList<User> mCacheUsers = new ArrayList<>();

        @Override
        public void cacheUser(User user) {
            if (getUser(user.getUserId()) != null) {
                mCacheUsers.add(user);
            }
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User cacheUser : mCacheUsers) {
                if (cacheUser.getUserId().equals(userId)) {
                    return cacheUser;
                }
            }

            return null;
        }
    }
}