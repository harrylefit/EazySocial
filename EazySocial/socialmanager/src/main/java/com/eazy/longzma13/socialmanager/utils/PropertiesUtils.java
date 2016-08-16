package com.eazy.longzma13.socialmanager.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Harry on 8/15/16.
 */

public class PropertiesUtils {
    private static Properties properties;
    private static final String fileDir = "social.properties";

    public static Properties getProperties(Context context) {
        if (properties == null) {
            properties = new Properties();

            try {
                AssetManager am = context.getAssets();
                //opening the file
                //opening the file
                InputStream inputStream = am.open(fileDir);
                //loading of the properties
                properties.load(inputStream);
            } catch (IOException e) {
                Log.e("PropertiesReader", e.toString());
            }
        } else {

        }
        return properties;
    }

}
