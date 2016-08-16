package com.eazy.longzma13.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eazy.longzma13.socialmanager.google.GoogleManager;
import com.eazy.longzma13.socialmanager.twitter.TwitterManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import twitter4j.auth.AccessToken;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TwitterManager.OnActionWhenTokenSuccessed, GoogleManager.OnGoogleSignInEvent {
    private Button btnTwitterLogin;
    private TwitterManager twitterManager;
    private Button btnGoogleLogin;
    private Button btnFacebookLogin;
    private Button btnLinkedinLogin;

    private GoogleManager googleManager;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO Twitter
        twitterManager = new TwitterManager(getApplicationContext());
        twitterManager.setOnActionWhenTokenSuccessed(this);
        btnTwitterLogin = (Button) findViewById(R.id.btnTwitterLogin);
        btnTwitterLogin.setOnClickListener(this);

        //TODO Google
        btnGoogleLogin = (Button) findViewById(R.id.btnGoogleLogin);
        googleManager = new GoogleManager();
        googleManager.setOnGoogleSignInEvent(this);
        btnGoogleLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTwitterLogin:
                twitterManager.login(this);
                break;
            case R.id.btnGoogleLogin:
                googleManager.login(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterManager.onActivityResult(requestCode, resultCode, data);
        googleManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getTokenTwitterSuccess(AccessToken accessToken) {
        Log.d("Test", "Twitter success");
    }

    @Override
    public void getTokenTwitterFailed() {
        Log.d("Test", "Twitter failed");
    }

    @Override
    public void onSuccessSignIn(GoogleSignInAccount googleSignInAccount) {
        Log.d("Test", "Google success");
    }

    @Override
    public void onFailedSignIn() {
        Log.d("Test", "Google failed");
    }
}
