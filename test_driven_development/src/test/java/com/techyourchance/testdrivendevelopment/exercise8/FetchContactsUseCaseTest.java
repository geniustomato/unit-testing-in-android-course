package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Assert;
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

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    public static final String FILTER_TERM = "filterTerm";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";

    private FetchContactsUseCase SUT;

    @Captor
    private ArgumentCaptor<String> mAcFilterTerm;
    @Captor
    private ArgumentCaptor<List<Contact>> mAcContactList;

    @Mock
    private GetContactsHttpEndpoint mGetContactsHttpEndpoint;

    @Mock
    private FetchContactsUseCase.Listener mMockListener1;

    @Mock
    private FetchContactsUseCase.Listener mMockListener2;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpoint);
        success();
    }

    private List<Contact> getContactItems() {
        ArrayList<Contact> contactItems = new ArrayList<>();
        contactItems.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contactItems;
    }


    @Test
    public void fetchContacts_passingArguments_correctArgumentsPassed() throws Exception {
        SUT.fetchContacts(FILTER_TERM);

        verify(mGetContactsHttpEndpoint).getContacts(mAcFilterTerm.capture(), any(Callback.class));

        Assert.assertThat(mAcFilterTerm.getValue(), is(FILTER_TERM));
    }

    @Test
    public void fetchContacts_success_observerNotifiedWithCorrectData() throws Exception {
        SUT.registerListener(mMockListener1);
        SUT.registerListener(mMockListener2);

        SUT.fetchContacts(FILTER_TERM);

        verify(mMockListener1).onSuccess(mAcContactList.capture());
        verify(mMockListener2).onSuccess(mAcContactList.capture());


        List<List<Contact>> receivedContactLists = mAcContactList.getAllValues();

        Assert.assertThat(receivedContactLists.get(0), is(getContactItems()));
        Assert.assertThat(receivedContactLists.get(1), is(getContactItems()));
    }

    @Test
    public void fetchContacts_success_unregisteredObserversNotNotified() throws Exception {
        SUT.registerListener(mMockListener1);
        SUT.registerListener(mMockListener2);
        SUT.unregisterListener(mMockListener2);

        SUT.fetchContacts(FILTER_TERM);


        verify(mMockListener1).onSuccess(mAcContactList.capture());
        verifyNoMoreInteractions(mMockListener2);
    }

    // Failure -- registered listeners receive Failure status
    @Test
    public void fetchContacts_failure_observersNotifiedWithFailure() throws Exception {
        generalError();

        SUT.registerListener(mMockListener1);
        SUT.registerListener(mMockListener2);

        SUT.fetchContacts(FILTER_TERM);


        verify(mMockListener1).onSuccess(mAcContactList.capture());
        verifyNoMoreInteractions(mMockListener2);
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactItems());

                return null;
            }
        }).when(mGetContactsHttpEndpoint).getContacts(any(String.class), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactItems());

                return null;
            }
        }).when(mGetContactsHttpEndpoint).getContacts(any(String.class), any(Callback.class));
    }

    // Failure -- unregistered listeners don't receive Failure status
    // Network error -- registered listeners receive Network Error status


}