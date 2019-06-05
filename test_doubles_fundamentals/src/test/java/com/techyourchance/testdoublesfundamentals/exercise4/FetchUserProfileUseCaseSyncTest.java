package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult.FAILURE;
import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR;
import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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

    @Test
    public void userSync_success_userIdPassedToEndpoint() {
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSync.mUserId, is(USER_ID));
    }

    @Test
    public void userSync_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCache.getUser(USER_ID).getUserId(), is(USER_ID));
    }

    @Test
    public void userSync_authError_userNotCached() {
        mUserProfileHttpEndpointSync.isAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCache.getUser(USER_ID), is(nullValue()));
    }


    @Test
    public void userSync_generalError_userNotCached() {
        mUserProfileHttpEndpointSync.isGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCache.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void userSync_serverError_userNotCached() {
        mUserProfileHttpEndpointSync.isServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCache.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void userSync_networkError_userNotCached() {
        mUserProfileHttpEndpointSync.isNetworkException = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCache.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void userSync_success_successReturned() {
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(SUCCESS));
    }

    @Test
    public void userSync_authError_failureReturned() {
        mUserProfileHttpEndpointSync.isAuthError = true;
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FAILURE));
    }

    @Test
    public void userSync_generalError_failureReturned() {
        mUserProfileHttpEndpointSync.isGeneralError = true;
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FAILURE));
    }

    @Test
    public void userSync_serverError_failureReturned() {
        mUserProfileHttpEndpointSync.isServerError = true;
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FAILURE));
    }

    @Test
    public void userSync_networkException_networkErrorReturned() {
        mUserProfileHttpEndpointSync.isNetworkException = true;
        mUsersCache.cacheUser(new User(USER_ID, FULL_NAME, IMAGE_URL));
        UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(NETWORK_ERROR));
    }


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
            if (getUser(user.getUserId()) == null) {
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