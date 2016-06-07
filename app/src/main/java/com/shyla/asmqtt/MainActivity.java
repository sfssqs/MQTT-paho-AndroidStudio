package com.shyla.asmqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shyla.security.CustomSecurity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MQTT.Main";

    private RemoteControl mRemoteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_subscribe).setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);
        findViewById(R.id.btn_ssl).setOnClickListener(this);

        RemoteControl.createInstance(getApplicationContext());
        mRemoteControl = RemoteControl.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteControl.registerResources(this);
    }

    @Override
    protected void onPause() {
        mRemoteControl.unregisterResources();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        RemoteControl.destroyInstance();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                mRemoteControl.connect();
                break;
            case R.id.btn_subscribe:
                mRemoteControl.subscribe();
                break;
            case R.id.btn_publish:
                mRemoteControl.publish();
                break;
            case R.id.btn_disconnect:
                mRemoteControl.disconnect();
                break;
            case R.id.btn_ssl:
                CustomSecurity security = new CustomSecurity(getApplicationContext());
                security.keyStore();
                break;
        }
    }
}
