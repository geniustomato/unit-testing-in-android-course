package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.FetchContactsUseCase.Listener;
import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    public static final String FILTER_NAME = "name";
    public static final String ID = "ID";
    public static final String FULL_NAME = "fullName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String IMAGE_URL = "imageUrl";
    public static final int AGE = 26;

    private FetchContactsUseCase SUT;

    @Mock
    private GetContactsHttpEndpoint mGetContactsHttpEndpointMock;

    @Mock
    private Listener mListener1;

    @Mock
    private Listener mListener2;

    @Captor
    private ArgumentCaptor<String> mStringAc;

    @Captor
    private ArgumentCaptor<List<Contact>> mContactAc;

    public FetchContactsUseCaseTest() {
    }

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpointMock);
        success();
    }

    @Test
    public void fetchContacts_success_correctFilterNamePassedToEndpoint() throws Exception {
        SUT.fetchContacts(FILTER_NAME);

        verify(mGetContactsHttpEndpointMock).getContacts(mStringAc.capture(), any(Callback.class));

        assertEquals(FILTER_NAME, mStringAc.getValue());
    }

    // success registered listeners informed of success and correct data from callback
    @Test
    public void fetchContacts_success_successCallbackWithDataCalled() throws Exception {
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchContacts(FILTER_NAME);


        verify(mListener1).onSuccess(mContactAc.capture());
        verify(mListener2).onSuccess(mContactAc.capture());

        List<List<Contact>> capturedContacts = mContactAc.getAllValues();

        assertEquals(getContactItemList(), capturedContacts.get(0));
        assertEquals(getContactItemList(), capturedContacts.get(1));
    }

    // unregistered listeners should not be triggered on success
    @Test
    public void fetchContacts_success_unregisteredListenersNotCalled() throws Exception {
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.unregisterListener(mListener1);

        SUT.fetchContacts(FILTER_NAME);


        verifyZeroInteractions(mListener1);
        verify(mListener2).onSuccess(mContactAc.capture());

        List<List<Contact>> capturedContacts = mContactAc.getAllValues();
        assertEquals(getContactItemList(), capturedContacts.get(0));
    }

    // general error registered listeners get failure callback
    @Test
    public void fetchContacts_generalError_endpointReturnsGeneralError() throws Exception {
        generalError();

        SUT.registerListener(mListener1);
        SUT.fetchContacts(FILTER_NAME);

        verify(mListener1).onFailure(EndpointResult.GENERAL_ERROR);
    }

    @Test
    public void fetchContacts_networkError_endpointReturnsNetworkError() throws Exception {
        networkError();

        SUT.registerListener(mListener1);
        SUT.fetchContacts(FILTER_NAME);

        verify(mListener1).onFailure(EndpointResult.NETWORK_ERROR);
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactSchema());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }


    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactSchema() {
        List<ContactSchema> contactSchemaList = new ArrayList<>();
        contactSchemaList.add(new ContactSchema(
                ID,
                FULL_NAME,
                PHONE_NUMBER,
                IMAGE_URL,
                AGE
        ));

        return contactSchemaList;
    }

    private List<Contact> getContactItemList() {
        List<Contact> contactList = new ArrayList<>();

        contactList.add(
                new Contact(
                        ID,
                        FULL_NAME,
                        IMAGE_URL
                )
        );
        return contactList;
    }


}