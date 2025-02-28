package com.amazon.alexa.auto.setup.workflow.fragment;

import static com.amazon.alexa.auto.apps.common.util.EarconSoundSettingsProvider.EARCON_SETTINGS;
import static com.amazon.alexa.auto.apps.common.util.EarconSoundSettingsProvider.EARCON_SETTINGS_END;
import static com.amazon.alexa.auto.apps.common.util.EarconSoundSettingsProvider.EARCON_SETTINGS_START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.amazon.alexa.auto.apis.app.AlexaApp;
import com.amazon.alexa.auto.apis.app.AlexaAppRootComponent;
import com.amazon.alexa.auto.apis.auth.AuthController;
import com.amazon.alexa.auto.apis.auth.AuthState;
import com.amazon.alexa.auto.apis.auth.AuthWorkflowData;
import com.amazon.alexa.auto.apis.login.LoginUIEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.subjects.PublishSubject;

@RunWith(RobolectricTestRunner.class)
public class CBLViewModelTest {
    private CBLViewModel mClassUnderTest;

    @Mock
    Application mMockApplication;
    @Mock
    Context mMockContext;
    @Mock
    AlexaApp mMockAlexaApp;
    @Mock
    AlexaAppRootComponent mMockRootComponent;
    @Mock
    SharedPreferences mMockSharedPrefs;
    @Mock
    SharedPreferences.Editor mMockEditor;
    @Mock
    AuthController mMockAuthController;
    @Mock
    LoginUIEventListener mMockLoginHostBinding;

    PublishSubject<AuthWorkflowData> mAuthWorkflowSubject = PublishSubject.create();

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        try (MockedStatic<AlexaApp> staticMock = Mockito.mockStatic(AlexaApp.class)) {
            staticMock.when(() -> AlexaApp.from(mMockApplication)).thenReturn(mMockAlexaApp);
            when(mMockAlexaApp.getRootComponent()).thenReturn(mMockRootComponent);
            when(mMockApplication.getApplicationContext()).thenReturn(mMockContext);
            when(mMockRootComponent.getAuthController()).thenReturn(mMockAuthController);
            when(mMockRootComponent.getComponent(LoginUIEventListener.class))
                    .thenReturn(Optional.of(mMockLoginHostBinding));
            when(mMockAuthController.newAuthenticationWorkflow()).thenReturn(mAuthWorkflowSubject);
            when(mMockContext.getSharedPreferences(EARCON_SETTINGS, 0)).thenReturn(mMockSharedPrefs);
            when(mMockSharedPrefs.edit()).thenReturn(mMockEditor);

            mClassUnderTest = new CBLViewModel(mMockApplication);
        }
    }

    @Test
    public void testStartLoginStartsNewAuthWorkflow() {
        mClassUnderTest.startLogin();

        verify(mMockAuthController, times(1)).newAuthenticationWorkflow();
        verify(mMockEditor, times(1)).putBoolean(EARCON_SETTINGS_START, true);
        verify(mMockEditor, times(1)).putBoolean(EARCON_SETTINGS_END, true);
    }

    @Test
    public void testAfterClearingViewModelNoNotificationsAreSent() {
        mClassUnderTest.startLogin();
        mClassUnderTest.onCleared();

        AuthWorkflowData authStarted = new AuthWorkflowData(AuthState.CBL_Auth_Started, null, null);
        mAuthWorkflowSubject.onNext(authStarted);

        assertNull(mClassUnderTest.loginWorkflowState().getValue());
    }

    @Test
    public void testLoginWorkflowStateChangesToLoginStartFailedAfterTimeout() {
        mClassUnderTest.startLogin();

        AuthWorkflowData authNotStarted = new AuthWorkflowData(AuthState.CBL_Auth_Not_Started, null, null);
        mAuthWorkflowSubject.onNext(authNotStarted);

        // Idle main looper to simulate not having for login started and timeout is kicked in.
        ShadowLooper.idleMainLooper(LoginViewModel.WAIT_FOR_LOGIN_START_MS, TimeUnit.MILLISECONDS);

        AuthWorkflowData authStartedFailed = new AuthWorkflowData(AuthState.CBL_Auth_Start_Failed, null, null);

        assertEquals(authStartedFailed, mClassUnderTest.loginWorkflowState().getValue());
    }

    @Test
    public void testLoginWorkflowStateChangesToLoginStartedBeforeTimeout() {
        mClassUnderTest.startLogin();

        AuthWorkflowData authNotStarted = new AuthWorkflowData(AuthState.CBL_Auth_Not_Started, null, null);
        mAuthWorkflowSubject.onNext(authNotStarted);

        // Idle main looper to wait for half of login start timeout.
        ShadowLooper.idleMainLooper(LoginViewModel.WAIT_FOR_LOGIN_START_MS / 2, TimeUnit.MILLISECONDS);

        AuthWorkflowData authStarted = new AuthWorkflowData(AuthState.CBL_Auth_Started, null, null);
        mAuthWorkflowSubject.onNext(authStarted);

        assertEquals(authStarted, mClassUnderTest.loginWorkflowState().getValue());
    }
}
