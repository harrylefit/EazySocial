package com.eazy.longzma13.socialmanager.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.eazy.longzma13.socialmanager.common.BasicSocialManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Harry on 7/1/16.
 */

public class FacebookManager implements BasicSocialManager {
    private final String TAG = "FacebookManager";
    private final String TOKEN_FB_KEY = "facebook_token";
    public static final String[] FACEBOOK_PERMISSION = {"public_profile", "email", "user_friends"};
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private Context context;

    private FacebookLoginCallback mFacebookLoginCallback;
    private OnFacebookEvent onFacebookEvent;
    private OnFacebookShareEvent onFacebookShareEvent;


    private void setUpShareConfig(String url, ShareDialog shareDialog) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .build();
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    if (onFacebookShareEvent != null) {
                        onFacebookShareEvent.onShareSuccessFacebook();
                    }
                }

                @Override
                public void onCancel() {
                    if (onFacebookShareEvent != null) {
                        onFacebookShareEvent.onShareCanceledFacebook();
                    }
                }

                @Override
                public void onError(FacebookException error) {
                    error.printStackTrace();
                }
            });
            shareDialog.show(shareLinkContent);

        }
    }

    @Override
    public void share(final String url, Fragment fragment) {
        ShareDialog shareDialog = new ShareDialog(fragment);
        setUpShareConfig(url, shareDialog);
    }


    public interface OnFacebookEvent {
        void onFacebookSuccess(LoginResult loginResult);

        void onFacebookFailed();
    }

    public interface OnFacebookShareEvent {
        void onShareSuccessFacebook();

        void onShareCanceledFacebook();
    }

    public FacebookManager(CallbackManager callbackManager, LoginManager loginManager) {
        this.callbackManager = callbackManager;
        this.loginManager = loginManager;
    }

    private class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            String fbAccessToken = loginResult.getAccessToken().getToken();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(TOKEN_FB_KEY,fbAccessToken);
            if (onFacebookEvent != null) {
                onFacebookEvent.onFacebookSuccess(loginResult);
            }
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, error.getMessage());
        }
    }

    @Override
    public void login(Activity activity) {
        if (mFacebookLoginCallback == null) {
            mFacebookLoginCallback = new FacebookLoginCallback();
        }
        this.context = activity.getApplicationContext();
        facebookLogin(activity, callbackManager, Arrays.asList(FACEBOOK_PERMISSION),
                mFacebookLoginCallback);
    }

    @Override
    public void login(Fragment fragment) {
        if (mFacebookLoginCallback == null) {
            mFacebookLoginCallback = new FacebookLoginCallback();
        }
        this.context = fragment.getContext();
        facebookLogin(fragment, callbackManager, Arrays.asList(FACEBOOK_PERMISSION),
                mFacebookLoginCallback);
    }

    @Override
    public void share(String msg, Activity activity) {
        ShareDialog shareDialog = new ShareDialog(activity);
        setUpShareConfig(msg, shareDialog);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void facebookLogin(Fragment fragment, CallbackManager callbackManager, List<String> permission,
                               FacebookCallback<LoginResult> facebookCallback) {
        loginManager.registerCallback(callbackManager, facebookCallback);
        loginManager.logInWithReadPermissions(fragment, permission);
    }

    private void facebookLogin(Activity activity, CallbackManager callbackManager, List<String> permission,
                               FacebookCallback<LoginResult> facebookCallback) {
        loginManager.registerCallback(callbackManager, facebookCallback);
        loginManager.logInWithReadPermissions(activity, permission);
    }

    public void setOnFacebookEvent(OnFacebookEvent onFacebookEvent) {
        this.onFacebookEvent = onFacebookEvent;
    }

    public void setOnFacebookShareEvent(OnFacebookShareEvent onFacebookShareEvent) {
        this.onFacebookShareEvent = onFacebookShareEvent;
    }

}
