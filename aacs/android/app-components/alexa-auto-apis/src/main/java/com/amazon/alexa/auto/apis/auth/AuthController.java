package com.amazon.alexa.auto.apis.auth;

import io.reactivex.rxjava3.core.Observable;

/**
 * API interface to let Alexa App know about the current auth state and
 * to start authentication related workflow such as new login, profile
 * selection etc.
 */
public interface AuthController {
    /**
     * Set authorization mode, such as CBL login mode, preview mode, etc.
     *
     * @param authMode Authorization mode.
     */
    void setAuthMode(AuthMode authMode);

    /**
     * Get authorization mode, such as CBL login mode, preview mode, etc.
     *
     * @return authMode Authorization mode.
     */
    AuthMode getAuthMode();

    /**
     * Tells if the user has authenticated within the current app.
     *
     * @return true, if user has been authenticated with current app.
     */
    boolean isAuthenticated();

    /**
     * Set the auth status when needed.
     *
     * @param  authStatus, auth status.
     */
    void setAuthState(AuthStatus authStatus);

    /**
     * Provides the observable for New Authentication workflow.
     * The authentication workflow will be started only after the client
     * subscribes to the returned Observable.
     * The workflow can ba cancelled anytime by simply un-subscribing from
     * the Observable
     *
     * @return Observable to monitor the progress of authentication workflow.
     */
    Observable<AuthWorkflowData> newAuthenticationWorkflow();

    /**
     * Fetch observable that can be used to monitor the auth changes.
     *
     * @return Observable of auth status.
     */
    Observable<AuthStatus> observeAuthChangeOrLogOut();

    /**
     * Logs out the currently logged in user.
     */
    void logOut();

    /**
     * User request to cancel login with current auth mode.
     */
    void cancelLogin(AuthMode mode);

    /**
     * Get user identity when login with CBL.
     *
     * @return UserIdentity User identity.
     */
    UserIdentity getUserIdentity();
}
