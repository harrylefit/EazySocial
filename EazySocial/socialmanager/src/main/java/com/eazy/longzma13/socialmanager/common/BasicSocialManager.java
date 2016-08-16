package com.eazy.longzma13.socialmanager.common;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Harry on 7/23/16.
 */

public interface BasicSocialManager {
    void login(Activity activity);
    void login(Fragment fragment);
    void share(String msg, Activity activity);
    void share(String msg, Fragment fragment);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
