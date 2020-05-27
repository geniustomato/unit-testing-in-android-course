package com.techyourchance.unittesting.questions;

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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    public static final String QUESTION_ID = "questionId";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String ID = "Id";

    private FetchQuestionDetailsUseCase SUT;

    private EndpointTd mFetchQuestionDetailsEndpointTd;

    @Mock
    private Listener mListener1;

    @Mock
    private Listener mListener2;

    @Captor
    private ArgumentCaptor<QuestionDetails> mQuestionDetailsAc;


    @Before
    public void setUp() throws Exception {
        mFetchQuestionDetailsEndpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(mFetchQuestionDetailsEndpointTd);
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

        assertEquals(getExpectedQuestionDetails(), capturedQuestionDetailsList.get(0));
        assertEquals(getExpectedQuestionDetails(), capturedQuestionDetailsList.get(1));
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

    private void failure() {
        mFetchQuestionDetailsEndpointTd.isGeneralError = true;
    }

    private QuestionDetails getExpectedQuestionDetails() {
        return new QuestionDetails(ID, TITLE, BODY);
    }

    private class EndpointTd extends FetchQuestionDetailsEndpoint {
        private String questionId;
        public boolean isGeneralError = false;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            this.questionId = questionId;

            if(isGeneralError) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                listener.onQuestionDetailsFetched(getQuestionSchema());
            }
        }

        private QuestionSchema getQuestionSchema() {
            return new QuestionSchema(TITLE, ID, BODY);
        }
    }

}