package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

public class FetchQuestionDetailsUseCase extends BaseObservable<FetchQuestionDetailsUseCase.Listener> {


    public static final long TIMEOUT = 60000L;

    public interface Listener {
        void onQuestionDetailsFetched(QuestionDetails questionDetails);

        void onQuestionDetailsFetchFailed();
    }

    private final FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpoint;
    private final TimeProvider mTimeProvider;
    private QuestionSchema mQuestionSchemaCache;
    private long mLastFetchTimestamp = 0L;

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId) {
        if (mQuestionSchemaCache == null || mTimeProvider.getCurrentTimestamp() >= mLastFetchTimestamp + TIMEOUT) {
            fetch(questionId);
        } else {
            notifySuccess(mQuestionSchemaCache);
        }
    }

    private void fetch(String questionId) {
        mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener() {
            @Override
            public void onQuestionDetailsFetched(QuestionSchema question) {
                mQuestionSchemaCache = question;
                mLastFetchTimestamp = mTimeProvider.getCurrentTimestamp();
                notifySuccess(question);
            }

            @Override
            public void onQuestionDetailsFetchFailed() {
                notifyFailure();
            }
        });
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetchFailed();
        }
    }

    private void notifySuccess(QuestionSchema questionSchema) {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetched(
                    new QuestionDetails(
                            questionSchema.getId(),
                            questionSchema.getTitle(),
                            questionSchema.getBody()
                    ));
        }
    }
}
