package com.shyla.asmqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MQTT.Main";

    public static final String serverUri = "tcp://10.75.3.123:1883";
    public static final String clientId = "paho-mqtt-clientid-001";

    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setKeepAliveInterval(1000);
        mqttConnectOptions.setUserName("user-name:summer");
        String msg = "hahaha";
        mqttConnectOptions.setWill("topic:111", msg.getBytes(), 0, true);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setTraceEnabled(true);
        mqttAndroidClient.setTraceCallback(new MqttTraceHandler() {
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
        });

        mqttAndroidClient.setCallback(new MqttCallback() {
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
        });

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.v(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.v(TAG, "onFailure, " + throwable.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttAndroidClient.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mqttAndroidClient.close();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mqttAndroidClient == null)
            return;
        mqttAndroidClient.registerResources(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mqttAndroidClient == null)
            return;
        mqttAndroidClient.unregisterResources();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
