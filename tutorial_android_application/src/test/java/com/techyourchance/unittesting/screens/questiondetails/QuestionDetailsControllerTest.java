package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    public static final String QUESTION_ID = "questionId";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final QuestionDetails QUESTION_DETAILS = new QuestionDetails(
            QUESTION_ID,
            TITLE,
            BODY
    );


    private QuestionDetailsController SUT;

    private UseCaseTd mUseCaseTd;
    @Mock
    private ScreensNavigator mScreensNavigatorMock;

    @Mock
    private ToastsHelper mToastsHelperMock;

    @Mock
    private QuestionDetailsViewMvc mQuestionDetailsMvcMock;


    @Before
    public void setUp() throws Exception {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionDetailsController(
                mUseCaseTd,
                mScreensNavigatorMock,
                mToastsHelperMock
        );

        SUT.bindView(mQuestionDetailsMvcMock);
        SUT.bindQuestionId(QUESTION_DETAILS.getId());
    }

    @Test
    public void onStart_registerListeners() throws Exception {
        SUT.onStart();

        mUseCaseTd.verifyListenerRegistered(SUT);
        verify(mQuestionDetailsMvcMock).registerListener(SUT);
    }

    @Test
    public void onStop_unregisterListeners() throws Exception {
        SUT.onStart();
        SUT.onStop();

        verify(mQuestionDetailsMvcMock).unregisterListener(SUT);
        mUseCaseTd.verifyListenerUnregistered(SUT);
    }

    @Test
    public void onStart_success_loadQuestionDetails() throws Exception {
        SUT.onStart();

        verify(mQuestionDetailsMvcMock).showProgressIndication();
        assertThat(mUseCaseTd.getTriggerCounter(), is(1));
        verify(mQuestionDetailsMvcMock).hideProgressIndication();
        verify(mQuestionDetailsMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_failure_showError() throws Exception {
        generalError();

        SUT.onStart();

        verify(mQuestionDetailsMvcMock).hideProgressIndication();
        verify(mToastsHelperMock).showUseCaseError();
   }

    @Test
    public void onNavigateUpClicked_navigateUp() throws Exception {
        SUT.onNavigateUpClicked();

        verify(mScreensNavigatorMock).navigateUp();
    }

    private void generalError() {
        mUseCaseTd.generalError = true;
    }

    public class UseCaseTd extends FetchQuestionDetailsUseCase {


        public boolean generalError = false;
        private int counter = 0;

        public UseCaseTd() {
            super(null, null);
        }


        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            counter++;

            for(Listener listener : getListeners()) {
                if(generalError) {
                     listener.onQuestionDetailsFetchFailed();
                } else {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }
            }
        }

        public int getTriggerCounter() {
            return counter;
        }

        public void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate)
                    return;
            }
            throw new RuntimeException("Listener not registered");
        }

        public void verifyListenerUnregistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate)
                    throw new RuntimeException("Listener is registered");
            }
        }
    }
}