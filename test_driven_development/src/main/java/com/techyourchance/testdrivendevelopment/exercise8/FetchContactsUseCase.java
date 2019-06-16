package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    private GetContactsHttpEndpoint mGetContactsHttpEndpoint;
    private ArrayList<Listener> mListeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        mGetContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchContacts(String filterTerm) {
        mGetContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<Contact> contactItems) {
                for (Listener listener : mListeners) {
                    listener.onSuccess(contactItems);
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {

            }
        });
    }

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener removedListener) {
        mListeners.remove(removedListener);

    }

    public interface Listener {
        void onSuccess(List<Contact> contactsList);
        void onFailure();
    }
}
