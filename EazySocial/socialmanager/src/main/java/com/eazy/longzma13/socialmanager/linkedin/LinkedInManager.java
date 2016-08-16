package com.eazy.longzma13.socialmanager.linkedin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.eazy.longzma13.socialmanager.common.BasicSocialManager;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONObject;


/**
 * Created by Harry on 6/29/16.
 */

public class LinkedInManager implements BasicSocialManager {
    public static final String PROFILE_KEY = "profile_key";
    private final String TAG = "LinkedManager";
    private final String OAUTH_TOKEN = "oauth_token";
    private final String URL_PROFILE_REQUEST = "https://api.linkedin.com/v1/people/~";
    private final Context context;
    static final int LINKEDIN_REQUEST = 1;
    private OnDataLinkedinEvent onDataLinkedinEvent;
    private OnLinkedinShareEvent onLinkedinShareEvent;

    public void setOnLinkedinShareEvent(OnLinkedinShareEvent onLinkedinShareEvent) {
        this.onLinkedinShareEvent = onLinkedinShareEvent;
    }

    @Override
    public void share(final String url, final Fragment fragment) {
        if (getSession().isValid()) {
            String urlShare = "https://api.linkedin.com/v1/people/~/shares";
            String payload = "{ \n" +
                    "   \"comment\":\"" + url + "\"," +
                    "   \"visibility\":{ " +
                    "      \"code\":\"anyone\"" +
                    "   }" +
                    "}";

            APIHelper apiHelper = APIHelper.getInstance(fragment.getActivity().getBaseContext());
            apiHelper.postRequest(fragment.getActivity().getBaseContext(), urlShare, payload, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse apiResponse) {
                    if (onLinkedinShareEvent != null) {
                        Toast.makeText(fragment.getActivity().getBaseContext(), "You has shared successful", Toast.LENGTH_SHORT).show();
                        onLinkedinShareEvent.onShareSuccessLinkedin();
                    }
                }

                @Override
                public void onApiError(LIApiError LIApiError) {
                    if (onLinkedinShareEvent != null) {
                        onLinkedinShareEvent.onShareCanceledLinkedin();
                    }
                }
            });
        } else {
            setOnDataLinkedinEvent(new OnDataLinkedinEvent() {
                @Override
                public void onDataProfile(ProfileLinkedin profileLinkedin) {
                    share(url, fragment);
                }

                @Override
                public void onErrorLinkedin(LIApiError liApiError) {
                    if (onLinkedinShareEvent != null) {
                        onLinkedinShareEvent.onShareCanceledLinkedin();
                    }
                }
            });
            login(fragment);
        }
    }

    @Override
    public void share(final String url, final Activity activity) {
        if (getSession().isValid()) {
            String urlShare = "https://api.linkedin.com/v1/people/~/shares";
            String payload = "{" +
                    //Status Content
                    "\"comment\":\"" + url +
                    //Link Share
                    "\"," +
                    //Who can see this status
                    "\"visibility\":{" +
                    "    \"code\":\"anyone\"}" +
                    "}";

            APIHelper apiHelper = APIHelper.getInstance(activity.getApplicationContext());
            apiHelper.postRequest(activity.getApplicationContext(), urlShare, payload, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse apiResponse) {
                    if (onLinkedinShareEvent != null) {
                        Toast.makeText(activity.getBaseContext(), "You has shared successful", Toast.LENGTH_SHORT).show();
                        onLinkedinShareEvent.onShareSuccessLinkedin();
                    }
                }

                @Override
                public void onApiError(LIApiError LIApiError) {
                    if (onLinkedinShareEvent != null) {
                        onLinkedinShareEvent.onShareCanceledLinkedin();
                    }
                }
            });
        } else {
            setOnDataLinkedinEvent(new OnDataLinkedinEvent() {
                @Override
                public void onDataProfile(ProfileLinkedin profileLinkedin) {
                    share(url, activity);
                }

                @Override
                public void onErrorLinkedin(LIApiError liApiError) {
                    if (onLinkedinShareEvent != null) {
                        onLinkedinShareEvent.onShareCanceledLinkedin();
                    }
                }
            });
            login(activity);
        }
    }

    public interface OnDataLinkedinEvent {
        void onDataProfile(ProfileLinkedin profileLinkedin);

        void onErrorLinkedin(LIApiError liApiError);
    }

    public class ProfileLinkedin implements Parcelable {
        private String firstName;
        private String headline;
        private String id;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public String getHeadline() {
            return headline;
        }

        public String getId() {
            return id;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.firstName);
            dest.writeString(this.headline);
            dest.writeString(this.id);
            dest.writeString(this.lastName);
        }

        public ProfileLinkedin() {
        }

        protected ProfileLinkedin(Parcel in) {
            this.firstName = in.readString();
            this.headline = in.readString();
            this.id = in.readString();
            this.lastName = in.readString();
        }

        public final Creator<ProfileLinkedin> CREATOR = new Creator<ProfileLinkedin>() {
            @Override
            public ProfileLinkedin createFromParcel(Parcel source) {
                return new ProfileLinkedin(source);
            }

            @Override
            public ProfileLinkedin[] newArray(int size) {
                return new ProfileLinkedin[size];
            }
        };
    }

    public LinkedInManager(Context context) {
        this.context = context;
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    @Override
    public void login(final Fragment fragment) {
        LISessionManager.getInstance(fragment.getActivity().getBaseContext()).init(fragment.getActivity(), buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(OAUTH_TOKEN,
                        LISessionManager.getInstance(context).getSession().getAccessToken().toString()).commit();
                requestProfileInfo(context);

            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.e(TAG, "Error Linked oauth:" + error.toString());
            }
        }, true);
    }

    @Override
    public void login(final Activity activity) {
        LISessionManager.getInstance(activity.getApplicationContext()).init(activity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).edit().putString(OAUTH_TOKEN,
                        LISessionManager.getInstance(activity.getApplicationContext()).getSession().getAccessToken().toString()).commit();
                requestProfileInfo(context);
                Log.d(TAG, "Oauth token: " + LISessionManager.getInstance(activity.getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.e(TAG, "Error Linked oauth:" + error.toString());
            }
        }, true);
    }

    public boolean isValidSession(Activity activity) {
        return LISessionManager.getInstance(activity.getApplicationContext()).getSession().isValid();
    }

    public boolean isValidSession(Fragment fragment) {
        return LISessionManager.getInstance(fragment.getActivity().getBaseContext()).getSession().isValid();
    }

    public void clearSession(Fragment fragment) {
        LISessionManager.getInstance(fragment.getActivity().getBaseContext()).clearSession();
    }

    public void clearSession(Activity activity) {
        LISessionManager.getInstance(activity.getApplicationContext()).clearSession();
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(context).onActivityResult(activity, requestCode, resultCode, data);
    }

    public void onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(context).onActivityResult(fragment.getActivity(), requestCode, resultCode, data);
    }

    @Deprecated
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //nothing
    }

    public interface OnLinkedinShareEvent {
        void onShareSuccessLinkedin();

        void onShareCanceledLinkedin();
    }

    public boolean checkState() {
        if (LISessionManager.getInstance(context).getSession().getAccessToken() == null) {
            return false;
        }
        return true;
    }

    public LISession getSession() {
        return LISessionManager.getInstance(context).getSession();
    }

    public String getOauthToken() {
        return LISessionManager.getInstance(context).getSession().getAccessToken().toString();
    }


    public void setOnDataLinkedinEvent(OnDataLinkedinEvent onDataLinkedinEvent) {
        this.onDataLinkedinEvent = onDataLinkedinEvent;
    }

    private void requestProfileInfo(Context context) {
        if (onDataLinkedinEvent != null) {
            final APIHelper apiHelper = APIHelper.getInstance(context);
            apiHelper.getRequest(context, URL_PROFILE_REQUEST, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse apiResponse) {
                    JSONObject data = apiResponse.getResponseDataAsJson();
                    ProfileLinkedin profileLinkedin = new ProfileLinkedin();
                    profileLinkedin.firstName = data.optString("firstName");
                    profileLinkedin.lastName = data.optString("lastName");
                    profileLinkedin.headline = data.optString("headline");
                    profileLinkedin.id = data.optString("id");
                    onDataLinkedinEvent.onDataProfile(profileLinkedin);
                }

                @Override
                public void onApiError(LIApiError LIApiError) {
                    onDataLinkedinEvent.onErrorLinkedin(LIApiError);
                }
            });

        }

    }

}
