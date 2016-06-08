package com.shyla.asmqtt;

import android.content.Context;
import android.util.Log;

import com.shyla.security.SslUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by xiaxing on 16-6-7.
 */
public class RemoteControl {
    public static final String TAG = "RemoteControl";

    public static final String URI_DEFAULT = "tcp://10.75.3.123:1883";
    public static final String URI_SSL = "ssl://10.75.3.123:8883";
    public static final String clientId = "paho-mqtt-clientid-001";

    private MqttAndroidClient mMqttAndroidClient;
    private Context mContext;
    private String requestUri;

    public RemoteControl(Context context) {
        mContext = context;
    }

    private static RemoteControl sInstance = null;

    public static RemoteControl createInstance(Context context) {
        if (sInstance == null)
            sInstance = new RemoteControl(context);

        return sInstance;
    }

    public static void destroyInstance() {
        if (sInstance == null)
            return;

        sInstance = null;
    }

    public static RemoteControl getInstance() {
        return sInstance;
    }

    public void registerResources(Context context) {
        if (mMqttAndroidClient == null)
            return;

        mMqttAndroidClient.registerResources(context);
    }

    public void unregisterResources() {
        if (mMqttAndroidClient == null)
            return;

        mMqttAndroidClient.unregisterResources();
    }

    public void connect(boolean ssl) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setKeepAliveInterval(1000);
        mqttConnectOptions.setUserName("user-name:summer");
        String msg = "hahaha";
        mqttConnectOptions.setWill("topic:111", msg.getBytes(), 0, true);

        if (ssl) {
            requestUri = URI_SSL;
            try {
                mqttConnectOptions.setSocketFactory(SslUtil.getSocketFactory(mContext));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestUri = URI_DEFAULT;
        }

        mMqttAndroidClient = new MqttAndroidClient(mContext.getApplicationContext(), requestUri, clientId);
        mMqttAndroidClient.setTraceEnabled(true);
        mMqttAndroidClient.setTraceCallback(new TraceHandler());
        mMqttAndroidClient.setCallback(mMqttCallback);

        try {
            mMqttAndroidClient.connect(mqttConnectOptions, null, mIMqttActionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mMqttAndroidClient.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void publish() {
        String sMsg = "lock";
        byte[] bMsg = sMsg.getBytes();
        String topic = "control";
        try {
            mMqttAndroidClient.publish(topic, bMsg, 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe() {
        try {
            String topic = "control";
            mMqttAndroidClient.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private IMqttActionListener mIMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            Log.v(TAG, "onSuccess");
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            Log.v(TAG, "onFailure, " + throwable.toString());
        }
    };

    private MqttCallback mMqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable throwable) {
            Log.v(TAG, "mqtt callback connectionLost");
            if (throwable != null)
                Log.v(TAG, "mqtt callback connectionLost : " + throwable.toString());
        }

        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            Log.v(TAG, "mqtt callback messageArrived, s : " + s + "throwable : " + mqttMessage.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            Log.v(TAG, "mqtt callback deliveryComplete, s : " + iMqttDeliveryToken.toString());
        }
    };
}
