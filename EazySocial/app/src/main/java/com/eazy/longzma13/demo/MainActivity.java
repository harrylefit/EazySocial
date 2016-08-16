package com.eazy.longzma13.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eazy.longzma13.socialmanager.facebook.FacebookManager;
import com.eazy.longzma13.socialmanager.google.GoogleManager;
import com.eazy.longzma13.socialmanager.twitter.TwitterManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import twitter4j.auth.AccessToken;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TwitterManager.OnActionWhenTokenSuccessed, GoogleManager.OnGoogleSignInEvent, FacebookManager.OnFacebookEvent {
    private Button btnTwitterLogin;
    private TwitterManager twitterManager;
    private Button btnGoogleLogin;
    private Button btnFacebookLogin;
    private Button btnLinkedinLogin;

    private FacebookManager facebookManager;
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

        //TODO Facebook
        btnFacebookLogin = (Button) findViewById(R.id.btnFacebookLogin);
        facebookManager = new FacebookManager();
        facebookManager.setOnFacebookEvent(this);
        btnFacebookLogin.setOnClickListener(this);
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
            case R.id.btnFacebookLogin:
                facebookManager.login(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterManager.onActivityResult(requestCode, resultCode, data);
        googleManager.onActivityResult(requestCode, resultCode, data);
        facebookManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getTokenTwitterSuccess(AccessToken accessToken) {
        Toast.makeText(getApplicationContext(), "Twitter success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getTokenTwitterFailed() {
        Toast.makeText(getApplicationContext(), "Twitter failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessSignIn(GoogleSignInAccount googleSignInAccount) {
        Toast.makeText(getApplicationContext(), "Google success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailedSignIn() {
        Toast.makeText(getApplicationContext(), "Google failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFacebookSuccess(LoginResult loginResult) {
        Toast.makeText(getApplicationContext(), "Facebook success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFacebookFailed() {
        Toast.makeText(getApplicationContext(), "Facebook failed", Toast.LENGTH_SHORT).show();
    }
}
