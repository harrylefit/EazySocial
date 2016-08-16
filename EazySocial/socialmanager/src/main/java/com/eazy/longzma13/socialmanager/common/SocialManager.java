package com.eazy.longzma13.socialmanager.common;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.eazy.longzma13.socialmanager.facebook.FacebookManager;
import com.eazy.longzma13.socialmanager.google.GoogleManager;
import com.eazy.longzma13.socialmanager.linkedin.LinkedInManager;
import com.eazy.longzma13.socialmanager.twitter.TwitterManager;


/**
 * Created by Harry on 7/23/16.
 */

public class SocialManager implements BasicSocialManager {
    private final FacebookManager mFacebookManager;
    private final GoogleManager mGoogleManager;
    private final LinkedInManager mLinkedInManager;
    private final TwitterManager mTwitterManager;

    private OnLoginSocialEvent mOnLoginSocialEvent;
    private OnShareSocialEvent mOnShareSocialEvent;
    private Activity mActivity;
    private Fragment mFragment;

    private DeprecatedException mDeprecatedException;
    private TargetNullException mTargetNullException;

    private LoginEventProvider mLoginEventProvider;
    private ShareEventProvider mShareEventProvider;

    private final String messageDeprecated = "Please call login() method instead of call this method";
    private final String messageTarget = "Please call setTarget before calling login or share method";

    public SocialManager setTarget(Fragment mFragment) {
        this.mFragment = mFragment;
        return this;
    }

    public SocialManager setTarget(Activity mActivity) {
        this.mActivity = mActivity;
        return this;
    }

    public Activity getTarget() {
        return this.mActivity;
    }

    public void login(Type type) {
        if (mFragment != null) {
            switch (type) {
                case GOOGLE:
                    mGoogleManager.login(mFragment);
                    break;
                case TWITTER:
                    mTwitterManager.login(mFragment);
                    break;
                case FACEBOOK:
                    mFacebookManager.login(mFragment);
                    break;
                case LINKEDIN:
                    mLinkedInManager.login(mFragment);
                    break;
            }
        } else if (mActivity != null) {
            switch (type) {
                case GOOGLE:
                    mGoogleManager.login(mActivity);
                    break;
                case TWITTER:
                    mTwitterManager.login(mActivity);
                    break;
                case FACEBOOK:
                    mFacebookManager.login(mActivity);
                    break;
                case LINKEDIN:
                    mLinkedInManager.login(mActivity);
                    break;
            }
        } else {
            try {
                throw getTargetNullException();
            } catch (TargetNullException e) {
                e.printStackTrace();
            }
        }
    }

    public void share(String msg, Type type) {
        if (mFragment != null) {
            switch (type) {
                case GOOGLE:
                    mGoogleManager.share(msg, mFragment);
                    break;
                case TWITTER:
                    mTwitterManager.share(msg, mFragment);
                    break;
                case FACEBOOK:
                    mFacebookManager.share(msg, mFragment);
                    break;
                case LINKEDIN:
                    mLinkedInManager.share(msg, mFragment);
                    break;
            }
        } else if (mActivity != null) {
            switch (type) {
                case GOOGLE:
                    mGoogleManager.share(msg, mActivity);
                    break;
                case TWITTER:
                    mTwitterManager.share(msg, mActivity);
                    break;
                case FACEBOOK:
                    mFacebookManager.share(msg, mActivity);
                    break;
                case LINKEDIN:
                    mLinkedInManager.share(msg, mActivity);
                    break;
            }
        } else {
            try {
                throw getTargetNullException();
            } catch (TargetNullException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    @Override
    public void login(Activity activity) {
        try {
            throw getDeprecatedException();
        } catch (DeprecatedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Override
    public void login(Fragment fragment) {
        try {
            throw getDeprecatedException();
        } catch (DeprecatedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Override
    public void share(String msg, Activity activity) {
        try {
            throw getDeprecatedException();
        } catch (DeprecatedException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Override
    public void share(String msg, Fragment fragment) {
        try {
            throw getDeprecatedException();
        } catch (DeprecatedException e) {
            e.printStackTrace();
        }
    }

    private class DeprecatedException extends Exception {
        public DeprecatedException(String msg) {
            super(msg);
        }
    }

    private class TargetNullException extends Exception {
        public TargetNullException(String msg) {
            super(msg);
        }
    }

    private TargetNullException getTargetNullException() {
        if (mTargetNullException == null) {
            mTargetNullException = new TargetNullException(messageTarget);
        }
        return mTargetNullException;
    }

    private DeprecatedException getDeprecatedException() {
        if (mDeprecatedException == null) {
            mDeprecatedException = new DeprecatedException(messageDeprecated);
        }
        return mDeprecatedException;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleManager.onActivityResult(requestCode, resultCode, data);
        mTwitterManager.onActivityResult(requestCode, resultCode, data);
        if (mFragment != null) {
            mLinkedInManager.onActivityResult(mFragment, requestCode, resultCode, data);
        } else if (mActivity != null) {
            mLinkedInManager.onActivityResult(mActivity, requestCode, resultCode, data);
        }
        mFacebookManager.onActivityResult(requestCode, resultCode, data);

    }

    public interface OnLoginSocialEvent {
        void loginSuccess(Type type, InfoSocial infoSocial);

        void loginFailed(Type type);
    }

    public interface OnShareSocialEvent {
        void shareSuccess(Type type);

        void shareFailed(Type type);
    }

    public SocialManager(FacebookManager mFacebookManager, GoogleManager mGoogleManager
            , LinkedInManager mLinkedInManager, TwitterManager mTwitterManager
            , LoginEventProvider mLoginEventProvider, ShareEventProvider shareEventProvider) {
        this.mFacebookManager = mFacebookManager;
        this.mGoogleManager = mGoogleManager;
        this.mLinkedInManager = mLinkedInManager;
        this.mTwitterManager = mTwitterManager;
        this.mLoginEventProvider = mLoginEventProvider;
        this.mShareEventProvider = shareEventProvider;
    }

    public void setOnLoginSocialEvent(OnLoginSocialEvent mOnLoginSocialEvent) {
        this.mOnLoginSocialEvent = mOnLoginSocialEvent;
        this.mLoginEventProvider.attachEvent(this.mOnLoginSocialEvent);
        mFacebookManager.setOnFacebookEvent(mLoginEventProvider.providerLoginFacebookEvent());
        mGoogleManager.setOnGoogleSignInEvent(mLoginEventProvider.providerLoginGoogleEvent());
        mLinkedInManager.setOnDataLinkedinEvent(mLoginEventProvider.providerLoginLinkedinEvent());
        mTwitterManager.setOnActionWhenTokenSuccessed(mLoginEventProvider.providerLoginTwitterEvent());
    }


    public void setOnShareSocialEvent(OnShareSocialEvent mOnShareSocialEvent) {
        this.mOnShareSocialEvent = mOnShareSocialEvent;
        this.mShareEventProvider.attachEvent(this.mOnShareSocialEvent);
        mFacebookManager.setOnFacebookShareEvent(mShareEventProvider.providerOnShareFacebookEvent());
        mGoogleManager.setOnGooglePlusShareEvent(mShareEventProvider.providerOnShareGoogleEvent());
        mTwitterManager.setOnShareEvent(mShareEventProvider.providerOnShareTwitterEvent());
        mLinkedInManager.setOnLinkedinShareEvent(mShareEventProvider.providerOnShareLinkedinEvent());
    }

    public void release() {
        this.mLoginEventProvider.release();
        this.mShareEventProvider.release();
    }
}
