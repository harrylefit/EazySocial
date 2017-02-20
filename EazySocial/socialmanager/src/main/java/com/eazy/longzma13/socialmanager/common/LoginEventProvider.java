package com.eazy.longzma13.socialmanager.common;

import com.eazy.longzma13.socialmanager.facebook.FacebookManager;
import com.eazy.longzma13.socialmanager.google.GoogleManager;
import com.eazy.longzma13.socialmanager.linkedin.LinkedInManager;
import com.eazy.longzma13.socialmanager.twitter.TwitterManager;
import com.linkedin.platform.errors.LIApiError;

import twitter4j.auth.AccessToken;

/**
 * Created by Harry on 7/24/16.
 */

public class LoginEventProvider implements SocialProvider {
    private InfoSocial mInfoSocial;
    private SocialManager.OnLoginSocialEvent mOnLoginSocialEvent;
    private OnLoginFacebookEvent onLoginFacebookEvent;
    private OnLoginGoogleEvent onLoginGoogleEvent;
    private OnLoginLinkedinEvent onLoginLinkedinEvent;
    private OnLoginTwitterEvent onLoginTwitterEvent;

    public void attachEvent(SocialManager.OnLoginSocialEvent mOnLoginSocialEvent) {
        this.mOnLoginSocialEvent = mOnLoginSocialEvent;
    }

    private InfoSocial getInfoSocial() {
        if (mInfoSocial == null) {
            mInfoSocial = new InfoSocial();
        }
        mInfoSocial.clearData();
        return mInfoSocial;
    }

    public void setLoginSuccesWithType(Type type, InfoSocial infoSocial) {
        if (mOnLoginSocialEvent != null) {
            mOnLoginSocialEvent.loginSuccess(type, infoSocial);
        }
    }

    public void setLoginFailedWithType(Type type) {
        if (mOnLoginSocialEvent != null) {
            mOnLoginSocialEvent.loginFailed(type);
        }
    }

    public OnLoginFacebookEvent providerLoginFacebookEvent() {
        this.onLoginFacebookEvent = new OnLoginFacebookEvent();
        return this.onLoginFacebookEvent;
    }

    public OnLoginTwitterEvent providerLoginTwitterEvent() {
        this.onLoginTwitterEvent = new OnLoginTwitterEvent();
        return this.onLoginTwitterEvent;
    }

    public OnLoginGoogleEvent providerLoginGoogleEvent() {
        this.onLoginGoogleEvent = new OnLoginGoogleEvent();
        return this.onLoginGoogleEvent;
    }

    public OnLoginLinkedinEvent providerLoginLinkedinEvent() {
        this.onLoginLinkedinEvent = new OnLoginLinkedinEvent();
        return this.onLoginLinkedinEvent;
    }

    @Override
    public void release() {
        this.mOnLoginSocialEvent = null;
        this.onLoginLinkedinEvent = null;
        this.onLoginFacebookEvent = null;
        this.onLoginTwitterEvent = null;
        this.onLoginGoogleEvent = null;
    }

    private class OnLoginFacebookEvent implements FacebookManager.OnFacebookEvent {

        @Override
        public void onFacebookSuccess(InfoSocial infoSocial) {
            setLoginSuccesWithType(Type.FACEBOOK, infoSocial);
        }

        @Override
        public void onFacebookFailed() {
            setLoginFailedWithType(Type.FACEBOOK);
        }
    }

    private class OnLoginGoogleEvent implements GoogleManager.OnGoogleSignInEvent {

        @Override
        public void onGoogleLoginSuccess(InfoSocial infoSocial) {
            setLoginSuccesWithType(Type.GOOGLE, infoSocial);
        }

        @Override
        public void onGoogleLoginFailed() {
            setLoginFailedWithType(Type.GOOGLE);
        }
    }

    private class OnLoginTwitterEvent implements TwitterManager.OnActionWhenTokenSuccessed {

        @Override
        public void getTokenTwitterSuccess(AccessToken accessToken) {
            InfoSocial infoSocial = getInfoSocial();
            infoSocial.setUserId(String.valueOf(accessToken.getUserId()));
            setLoginSuccesWithType(Type.TWITTER, infoSocial);
        }

        @Override
        public void getTokenTwitterFailed() {
            setLoginFailedWithType(Type.TWITTER);
        }
    }

    private class OnLoginLinkedinEvent implements LinkedInManager.OnDataLinkedinEvent {

        @Override
        public void onDataProfile(LinkedInManager.ProfileLinkedin profileLinkedin) {
            InfoSocial infoSocial = getInfoSocial();
            infoSocial.setUserId(profileLinkedin.getId());
            infoSocial.setFirstName(profileLinkedin.getFirstName());
            infoSocial.setLastName(profileLinkedin.getLastName());
            setLoginSuccesWithType(Type.LINKEDIN, infoSocial);
        }

        @Override
        public void onErrorLinkedin(LIApiError liApiError) {
            setLoginFailedWithType(Type.LINKEDIN);
        }
    }

}
