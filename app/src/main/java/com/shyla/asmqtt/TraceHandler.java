package com.shyla.asmqtt;

import android.util.Log;

import org.eclipse.paho.android.service.MqttTraceHandler;

/**
 * Created by xiaxing on 16-6-7.
 */
public class TraceHandler implements MqttTraceHandler {

    public static final String TAG = "TraceHandler";

    @Override
    public void traceDebug(String s, String s1) {
        Log.v(TAG, "mqtt trace debug, s : " + s + ", s1 : " + s1);
    }

    @Override
    public void traceError(String s, String s1) {
        Log.v(TAG, "mqtt trace error, s : " + s + ", s1 : " + s1);
    }

    @Override
    public void traceException(String s, String s1, Exception e) {
        e.printStackTrace();
        Log.v(TAG, "mqtt trace exception, s : " + s + ", e : " + e.toString());
    }
}
