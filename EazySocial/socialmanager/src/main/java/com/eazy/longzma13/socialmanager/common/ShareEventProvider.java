package com.eazy.longzma13.socialmanager.common;


import com.eazy.longzma13.socialmanager.facebook.FacebookManager;
import com.eazy.longzma13.socialmanager.google.GoogleManager;
import com.eazy.longzma13.socialmanager.linkedin.LinkedInManager;
import com.eazy.longzma13.socialmanager.twitter.TwitterManager;

/**
 * Created by Harry on 7/24/16.
 */

public class ShareEventProvider implements SocialProvider {
    private SocialManager.OnShareSocialEvent mOnShareSocialEvent;
    private OnShareFacebookEvent mOnShareFacebookEvent;
    private OnShareGoogleEvent mOnShareGoogleEvent;
    private OnShareTwitterEvent mOnShareTwitterEvent;
    private OnShareLinkedinEvent mOnShareLinkedinEvent;

    public void attachEvent(SocialManager.OnShareSocialEvent mOnShareSocialEvent) {
        this.mOnShareSocialEvent = mOnShareSocialEvent;
    }

    public OnShareFacebookEvent providerOnShareFacebookEvent() {
        this.mOnShareFacebookEvent = new OnShareFacebookEvent();
        return this.mOnShareFacebookEvent;
    }

    public OnShareLinkedinEvent providerOnShareLinkedinEvent() {
        this.mOnShareLinkedinEvent = new OnShareLinkedinEvent();
        return this.mOnShareLinkedinEvent;
    }

    public OnShareTwitterEvent providerOnShareTwitterEvent() {
        this.mOnShareTwitterEvent = new OnShareTwitterEvent();
        return this.mOnShareTwitterEvent;
    }

    public OnShareGoogleEvent providerOnShareGoogleEvent() {
        this.mOnShareGoogleEvent = new OnShareGoogleEvent();
        return this.mOnShareGoogleEvent;
    }

    public void setShareSuccesWithType(Type type) {
        if (mOnShareSocialEvent != null) {
            mOnShareSocialEvent.shareSuccess(type);
        }
    }

    public void setShareFailedWithType(Type type) {
        if (mOnShareSocialEvent != null) {
            mOnShareSocialEvent.shareFailed(type);
        }
    }

    private class OnShareFacebookEvent implements FacebookManager.OnFacebookShareEvent {

        @Override
        public void onShareSuccessFacebook() {
            setShareSuccesWithType(Type.FACEBOOK);
        }

        @Override
        public void onShareCanceledFacebook() {
            setShareFailedWithType(Type.FACEBOOK);
        }
    }

    private class OnShareGoogleEvent implements GoogleManager.OnGooglePlusShareEvent {

        @Override
        public void onShareSuccessGooglePlus() {
            setShareSuccesWithType(Type.GOOGLE);
        }

        @Override
        public void onShareCanceledGooglePlus() {
            setShareFailedWithType(Type.GOOGLE);
        }
    }

    private class OnShareLinkedinEvent implements LinkedInManager.OnLinkedinShareEvent {
        @Override
        public void onShareSuccessLinkedin() {
            setShareSuccesWithType(Type.LINKEDIN);
        }

        @Override
        public void onShareCanceledLinkedin() {
            setShareFailedWithType(Type.LINKEDIN);
        }
    }

    private class OnShareTwitterEvent implements TwitterManager.OnShareEvent {

        @Override
        public void onShareTwitterSuccess() {
            setShareSuccesWithType(Type.TWITTER);
        }

        @Override
        public void onShareTwitterFailed() {
            setShareFailedWithType(Type.TWITTER);
        }
    }

    @Override
    public void release() {
        this.mOnShareSocialEvent = null;
        this.mOnShareGoogleEvent = null;
        this.mOnShareTwitterEvent = null;
        this.mOnShareLinkedinEvent = null;
        this.mOnShareFacebookEvent = null;
    }
}
