package com.shyla;

import android.app.Application;
import android.util.Log;

/**
 * Created by xiaxing on 16-6-7.
 */

public class MqttApplication extends Application {

    @Override
    public void onCreate() {
        Log.d("MqttApplication", "onCreate  start");
        super.onCreate();
    }
}
