package com.eazy.longzma13.socialmanager.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


import com.eazy.longzma13.socialmanager.common.BasicSocialManager;
import com.eazy.longzma13.socialmanager.utils.PropertiesUtils;

import java.util.Properties;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by HarryLe on 4/10/16.
 */

public class TwitterManager implements BasicSocialManager {
    public static final int OAUTH_REQUEST = 2;
    public static final int OAUTH_SHARE_REQUEST = 3;
    public static final String PREFS = "prefs";
    private String consumerKey;
    private String consumerSecret;
    private TwitterBackend backend;
    private SharedPreferences prefs;
    private AccessToken token;
    private final Context context;
    private OnActionWhenTokenSuccessed onActionWhenTokenSuccessed;
    private OnShareEvent onShareEvent;


    public TwitterManager(Context context,String consumerKey,String consumerSecret){
        this.context = context;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        initializeVariables();
    }

    public TwitterManager(Context context) {
        this.context = context;
        this.consumerKey = PropertiesUtils.getProperties(context).getProperty("consumerKey");
        this.consumerSecret = PropertiesUtils.getProperties(context).getProperty("consumerSecret");
        initializeVariables();
    }

    public TwitterManager(Context context, Properties properties) {
        this.context = context;
        this.consumerKey = properties.getProperty("consumerKey");
        this.consumerSecret = properties.getProperty("consumerSecret");
        initializeVariables();
    }

    public void initializeVariables() {
        backend = new TwitterBackend(consumerKey, consumerSecret);
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }

    public void setUp(Fragment fragment) {
        if (tokenExists()) {
            getToken();
            backend.twitterInit(token);
            if (onActionWhenTokenSuccessed != null) {
                onActionWhenTokenSuccessed.getTokenTwitterSuccess(token);
            }
        } else {
            loginTw(fragment);
        }
    }


    public void setUp(Activity activity) {
        if (tokenExists()) {
            getToken();
            backend.twitterInit(token);
            if (onActionWhenTokenSuccessed != null) {
                onActionWhenTokenSuccessed.getTokenTwitterSuccess(token);
            }
        } else {
            loginTw(activity);
        }
    }

    public void loginTw(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), Browser.class);
        String url = backend.getAuthorizationURL();
        intent.putExtra("url", url);
        fragment.startActivityForResult(intent, OAUTH_REQUEST);
    }


    public void loginTw(Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), Browser.class);
        String url = backend.getAuthorizationURL();
        intent.putExtra("url", url);
        activity.startActivityForResult(intent, OAUTH_REQUEST);
    }

    public boolean tokenExists() {
        return prefs.contains("oauth_token") && prefs.contains("oauth_secret");
    }

    public void saveToken() {
        token = backend.getAccessToken();
        if (token != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("oauth_token", token.getToken());
            editor.putString("oauth_secret", token.getTokenSecret());
            editor.commit();
        }
    }

    public AccessToken getToken() {
        String accessToken = prefs.getString("oauth_token", null);
        String secret = prefs.getString("oauth_secret", null);
        if (accessToken != null && secret != null) {
            token = new AccessToken(accessToken, secret);
        }
        return token;
    }

    public void getDataSuccess(Intent data) {
        Uri url = Uri.parse(data.getExtras().getString("url"));
        String verifier = url.getQueryParameter("oauth_verifier");
        String token = url.getQueryParameter("oauth_token");

        if (context != null) {
            Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show();
        }
        backend.setAccessToken(verifier);
        backend.twitterInit();
        saveToken();

        if (onActionWhenTokenSuccessed != null) {
            onActionWhenTokenSuccessed.getTokenTwitterSuccess(backend.getAccessToken());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TwitterManager.OAUTH_REQUEST && resultCode == Activity.RESULT_OK) {
            getDataSuccess(data);
        } else if (requestCode == TwitterManager.OAUTH_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(context, "Cannot connect to twitter, app not authorized", Toast.LENGTH_SHORT).show();
        }
    }

    public void setToken(AccessToken token) {
        this.token = token;
    }

    public TwitterBackend getBackend() {
        return backend;
    }

    public void setBackend(TwitterBackend backend) {
        this.backend = backend;
    }

    public void setOnActionWhenTokenSuccessed(OnActionWhenTokenSuccessed onActionWhenTokenSuccessed) {
        this.onActionWhenTokenSuccessed = onActionWhenTokenSuccessed;
    }

    @Override
    public void share(final String url, final Activity activity) {
        if (!tokenExists()) {
            login(activity);
        } else {
            processTweet(url, activity);
        }

    }


    @Override
    public void share(final String url, final Fragment fragment) {
        if (!tokenExists()) {
            login(fragment);
        } else {
            processTweet(url, fragment.getActivity());
        }

    }

    private void processTweet(final String url, final Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setDebugEnabled(true);
                builder.setOAuthAccessToken(getToken().getToken());
                builder.setOAuthAccessTokenSecret(getToken().getTokenSecret());
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);
                Configuration configuration = builder.build();
                final TwitterFactory factory = new TwitterFactory(configuration);
                final Twitter twitter = factory.getInstance(getToken());
                if (twitter.getAuthorization().isEnabled()) {
                    Status status = null;
                    try {
                        status = twitter.updateStatus(url);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } finally {
                        if (status != null) {
                            if (onShareEvent != null) {
                                onShareEvent.onShareTwitterSuccess();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity.getBaseContext(), "Twitter shared success", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Log.d("test", "onshare success");
                            }
                        } else {
                            onShareEvent.onShareTwitterFailed();
                        }
                    }
                }
            }
        }).start();
    }

    public interface OnActionWhenTokenSuccessed {
        void getTokenTwitterSuccess(AccessToken accessToken);

        void getTokenTwitterFailed();
    }


    @Override
    public void login(Activity activity) {
        if (!checkNetwork()) {

            Toast.makeText(activity.getApplicationContext(), "Cannot connect to twitter.", Toast.LENGTH_SHORT).show();
        } else {
            setUp(activity);
        }
    }

    @Override
    public void login(Fragment fragment) {
        if (!checkNetwork()) {
            Toast.makeText(fragment.getContext(), "Cannot connect to twitter.", Toast.LENGTH_SHORT).show();
        } else {
            setUp(fragment);
        }
    }

    public interface OnShareEvent {
        void onShareTwitterSuccess();

        void onShareTwitterFailed();

    }

    public void setOnShareEvent(OnShareEvent mOnShareEvent) {
        this.onShareEvent = mOnShareEvent;
    }

}
