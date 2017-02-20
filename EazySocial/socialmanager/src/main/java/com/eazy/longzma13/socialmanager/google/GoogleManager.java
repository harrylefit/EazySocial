package com.eazy.longzma13.socialmanager.google;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.eazy.longzma13.socialmanager.common.BasicSocialManager;
import com.eazy.longzma13.socialmanager.common.InfoSocial;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusShare;

import java.lang.ref.WeakReference;


/**
 * Created by Harry on 7/1/16.
 */

public class GoogleManager implements GoogleApiClient.OnConnectionFailedListener, BasicSocialManager {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Google Manager";
    static final int PLUS_ONE_REQUEST = 0;
    private GoogleApiClient mGoogleApiClient;
    private WeakReference<FragmentActivity> activityWeakReference;
    private OnGoogleSignInEvent onGoogleSignInEvent;

    public void setOnGooglePlusShareEvent(OnGooglePlusShareEvent onGooglePlusShareEvent) {
        this.onGooglePlusShareEvent = onGooglePlusShareEvent;
    }

    private OnGooglePlusShareEvent onGooglePlusShareEvent;

    @Override
    public void share(String url, Fragment fragment) {
        activityWeakReference = new WeakReference<>(fragment.getActivity());
        Intent shareIntent = new PlusShare.Builder(fragment.getContext())
                .setContentUrl(Uri.parse(url))
                .setText("Share")
                .setType("text/plain")
                .getIntent();
        try {
            fragment.startActivityForResult(shareIntent, 0);
        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "You haven'dt installed google+ on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void share(String url, Activity activity) {
        activityWeakReference = new WeakReference<>((FragmentActivity) activity);
        Intent shareIntent = new PlusShare.Builder(activity.getBaseContext())
                .setContentUrl(Uri.parse(url))
                .setText("Share")
                .setType("text/plain")
                .getIntent();
        try {
            activity.startActivityForResult(shareIntent, 0);
        } catch (Exception e) {
            Toast.makeText(activity, "You haven'dt installed google+ on your device", Toast.LENGTH_SHORT).show();
        }
    }

    public void release() {
        if (activityWeakReference != null && activityWeakReference.get() != null && mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(activityWeakReference.get());
            mGoogleApiClient.disconnect();
        }
    }


    public interface OnGoogleSignInEvent {
        void onGoogleLoginSuccess(InfoSocial infoSocial);

        void onGoogleLoginFailed();
    }


    private void init(Fragment fragment) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.w3
        mGoogleApiClient = new GoogleApiClient.Builder(fragment.getContext())
                .enableAutoManage(fragment.getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void init(Activity activity) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.w3
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (onGoogleSignInEvent != null) {
                InfoSocial infoSocial = new InfoSocial();
                infoSocial.setName(acct.getDisplayName());
                infoSocial.setUserId(acct.getId());
                infoSocial.setEmail(acct.getEmail());
                infoSocial.setAccessToken(acct.getIdToken());
                onGoogleSignInEvent.onGoogleLoginSuccess(infoSocial);
            }
            Log.d(TAG, "Success googleplus");

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void login(Fragment fragment) {
        try {
            activityWeakReference = new WeakReference<>(fragment.getActivity());
            init(fragment);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            fragment.startActivityForResult(signInIntent, RC_SIGN_IN);
        }catch (Exception ex){
            Toast.makeText(fragment.getActivity().getApplicationContext(), "Already login in. You can not login again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void login(Activity activity) {
        try {
            activityWeakReference = new WeakReference<>((FragmentActivity) activity);
            init(activity);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception ex) {
            Toast.makeText(activity.getApplicationContext(), "Already login in. You can not login again.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkStatus() {
        if (onGoogleSignInEvent == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
            case PLUS_ONE_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //here the operation was successful
                        if (onGooglePlusShareEvent != null) {
                            onGooglePlusShareEvent.onShareSuccessGooglePlus();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        //here the user backed out or failed
                        if (onGooglePlusShareEvent != null) {
                            onGooglePlusShareEvent.onShareCanceledGooglePlus();
                        }
                        break;
                }
                break;
        }
    }

    public void setOnGoogleSignInEvent(OnGoogleSignInEvent onGoogleSignInEvent) {
        this.onGoogleSignInEvent = onGoogleSignInEvent;
    }

    public interface OnGooglePlusShareEvent {
        void onShareSuccessGooglePlus();

        void onShareCanceledGooglePlus();
    }
}
