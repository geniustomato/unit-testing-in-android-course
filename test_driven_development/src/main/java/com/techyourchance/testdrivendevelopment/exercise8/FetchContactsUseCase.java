package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    private ArrayList<Listener> mListeners = new ArrayList<>();

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }

    public interface Listener {
        void onSuccess(List<Contact> contacts);

        void onFailure(EndpointResult generalError);
    }

    private GetContactsHttpEndpoint mGetContactsHttpEndpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint mGetContactsHttpEndpoint) {
        this.mGetContactsHttpEndpoint = mGetContactsHttpEndpoint;
    }

    public void fetchContacts(String filterName) {
        mGetContactsHttpEndpoint.getContacts(filterName, new GetContactsHttpEndpoint.Callback() {

            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactItems) {
                for (Listener listener : mListeners) {
                    listener.onSuccess(mapContactSchema(contactItems));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                for (Listener listener : mListeners) {
                    switch (failReason) {
                        case GENERAL_ERROR:
                            listener.onFailure(EndpointResult.GENERAL_ERROR);
                            break;
                        case NETWORK_ERROR:
                            listener.onFailure(EndpointResult.NETWORK_ERROR);
                            break;
                        default:
                            throw new RuntimeException("Unhandled error");
                    }
                }
            }
        });
    }

    private List<Contact> mapContactSchema(List<ContactSchema> contactItems) {
        List<Contact> contactList = new ArrayList<>();
        for (ContactSchema contactSchema : contactItems) {
            contactList.add(
                    new Contact(
                            contactSchema.getId(),
                            contactSchema.getFullName(),
                            contactSchema.getImageUrl()
                    )
            );
        }

        return contactList;
    }
}
