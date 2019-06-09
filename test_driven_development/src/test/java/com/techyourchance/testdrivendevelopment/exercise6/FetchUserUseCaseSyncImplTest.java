package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.NETWORK_ERROR;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final User USER = new User(USER_ID, USERNAME);

    private FetchUserUseCaseSyncImpl SUT;

    @Mock
    private UsersCache mUsersCache;

    @Mock
    private FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(mUsersCache, mFetchUserHttpEndpointSync);
        success();
    }

    // If the user with given user ID is not in the cache then it should be fetched from the server.
    @Test
    public void fetchUser_notInCacheFetchFromServerSuccess_userReturned() {
        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        Assert.assertThat(result.getStatus(), is(SUCCESS));
        Assert.assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUser_notInCacheFetchFromServerAuthError_noUserReturned() throws Exception {
        authError();

        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        Assert.assertThat(result.getStatus(), is(FAILURE));
        Assert.assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUser_notInCacheFetchFromServerGeneralError_noUserReturned() throws Exception {
        generalError();

        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        Assert.assertThat(result.getStatus(), is(FAILURE));
        Assert.assertThat(result.getUser(), is(nullValue()));
    }

    @Test
    public void fetchUser_notInCacheFetchFromServerNetworkError_noUserReturned() throws Exception {
        networkError();

        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        Assert.assertThat(result.getStatus(), is(NETWORK_ERROR));
        Assert.assertThat(result.getUser(), is(nullValue()));
    }

    // If the user fetched from the server then it should be stored in the cache before returning to the caller.
    @Test
    public void fetchUser_notInCacheFetchFromServerSuccess_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.fetchUserSync(USER_ID);

        verify(mUsersCache).cacheUser(ac.capture());
        Assert.assertThat(ac.getValue().getUserId(), is(USER_ID));
    }

    @Test
    public void fetchUser_notInCacheFetchFromServerGeneralError_userNotCached() throws Exception {
        generalError();

        SUT.fetchUserSync(USER_ID);

        User result = verify(mUsersCache).getUser(USER_ID);
        Assert.assertThat(result, is(nullValue()));
    }

    @Test
    public void fetchUser_notInCacheFetchFromServerAuthError_userNotCached() throws Exception {
        authError();

        SUT.fetchUserSync(USER_ID);

        User result = verify(mUsersCache).getUser(USER_ID);
        Assert.assertThat(result, is(nullValue()));    }

    @Test
    public void fetchUser_notInCacheFetchFromServerNetworkError_userNotCached() throws Exception {
        networkError();

        SUT.fetchUserSync(USER_ID);

        User result = verify(mUsersCache).getUser(USER_ID);
        Assert.assertThat(result, is(nullValue()));
    }

    // If the user is in the cache then cached record should be returned without polling the server.
    @Test
    public void fetchUser_inCache_serverIsNotPolled() throws Exception {
        inCache();

        SUT.fetchUserSync(USER_ID);

        verifyNoMoreInteractions(mFetchUserHttpEndpointSync);
    }

    @Test
    public void fetchUser_inCache_correctParametersPassed() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        inCache();

        SUT.fetchUserSync(USER_ID);

        verify(mUsersCache).getUser(ac.capture());
        Assert.assertThat(ac.getValue(), is(USER_ID));
    }


    @Test
    public void fetchUser_inCache_cachedUserReturned() throws Exception {
        inCache();

        SUT.fetchUserSync(USER_ID);

        User cachedUser = mUsersCache.getUser(USER_ID);
        Assert.assertThat(cachedUser, is(USER));
    }

    private void inCache() {
        when(mUsersCache.getUser(any(String.class))).thenReturn(USER);
    }

    private void success() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync
                .fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync
                .fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void generalError() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync
                .fetchUserSync(USER_ID))
                .thenReturn(new FetchUserHttpEndpointSync.EndpointResult(FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync
                .fetchUserSync(USER_ID))
                .thenThrow(new NetworkErrorException());
    }
}