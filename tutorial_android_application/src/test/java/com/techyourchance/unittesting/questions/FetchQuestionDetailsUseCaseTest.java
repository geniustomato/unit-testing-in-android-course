package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase.Listener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    public static final String QUESTION_ID = "questionId";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String ID = "Id";

    public static final String UPDATED_TITLE = TITLE + "Updated";
    public static final String UPDATED_ID = ID + "Updated";
    public static final String UPDATED_BODY = BODY + "Updated";

    private FetchQuestionDetailsUseCase SUT;

    private EndpointTd mFetchQuestionDetailsEndpointTd;

    @Mock
    private Listener mListener1;

    @Mock
    private Listener mListener2;

    @Mock
    private TimeProvider mTimeProviderMock;

    @Captor
    private ArgumentCaptor<QuestionDetails> mQuestionDetailsAc;


    @Before
    public void setUp() throws Exception {
        mFetchQuestionDetailsEndpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(mFetchQuestionDetailsEndpointTd, mTimeProviderMock);
    }

    @Test
    public void fetchQuestionDetails_success_passesCorrectParametersToEndpoint() throws Exception {
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        assertThat(mFetchQuestionDetailsEndpointTd.questionId, is(QUESTION_ID));
    }

    @Test
    public void fetchQuestionDetails_success_returnsSuccessAndPassesCorrectData() throws Exception {
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        verify(mListener1).onQuestionDetailsFetched(mQuestionDetailsAc.capture());
        verify(mListener2).onQuestionDetailsFetched(mQuestionDetailsAc.capture());
        List<QuestionDetails> capturedQuestionDetailsList = mQuestionDetailsAc.getAllValues();

        assertEquals(getExpectedFirstQuestionDetails(), capturedQuestionDetailsList.get(0));
        assertEquals(getExpectedFirstQuestionDetails(), capturedQuestionDetailsList.get(1));
    }

    @Test
    public void fetchQuestionDetails_generalError_returnFailure() throws Exception {
        failure();

        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        verify(mListener1).onQuestionDetailsFetchFailed();
        verify(mListener2).onQuestionDetailsFetchFailed();
    }

    @Test
    public void fetchQuestionDetails_ifTriggeredAgainBeforeTimeout_returnCacheData() throws Exception {
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(59999L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        assertThat(mFetchQuestionDetailsEndpointTd.fetchCounter, is(1));
        verify(mListener1, times(2)).onQuestionDetailsFetched(getExpectedFirstQuestionDetails());
        verify(mListener2, times(2)).onQuestionDetailsFetched(getExpectedFirstQuestionDetails());
    }

    @Test
    public void fetchQuestionDetails_ifTriggeredAgainAfterTimeout_returnUpdatedDataFromEndpoint() throws Exception {
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        when(mTimeProviderMock.getCurrentTimestamp()).thenReturn(60000L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);

        assertThat(mFetchQuestionDetailsEndpointTd.fetchCounter, is(2));
        verify(mListener1).onQuestionDetailsFetched(getExpectedUpdatedQuestionDetails());
        verify(mListener2).onQuestionDetailsFetched(getExpectedUpdatedQuestionDetails());
    }

    private void failure() {
        mFetchQuestionDetailsEndpointTd.isGeneralError = true;
    }

    private QuestionDetails getExpectedFirstQuestionDetails() {
        return new QuestionDetails(ID, TITLE, BODY);
    }

    private QuestionDetails getExpectedUpdatedQuestionDetails() {
        return new QuestionDetails(UPDATED_ID, UPDATED_TITLE, UPDATED_BODY);
    }


    private class EndpointTd extends FetchQuestionDetailsEndpoint {
        private String questionId;

        public boolean isGeneralError = false;
        public int fetchCounter = 0;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            fetchCounter++;
            this.questionId = questionId;

            if(isGeneralError) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                if(fetchCounter == 1) {
                    listener.onQuestionDetailsFetched(getFirstQuestionSchema());
                } else {
                    listener.onQuestionDetailsFetched(getUpdatedQuestionSchema());
                }
            }
        }

        private QuestionSchema getFirstQuestionSchema() {
            return new QuestionSchema(TITLE, ID, BODY);
        }

        private QuestionSchema getUpdatedQuestionSchema() {
            return new QuestionSchema(UPDATED_TITLE, UPDATED_ID, UPDATED_BODY);
        }
    }

}