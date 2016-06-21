package com.shyla.main;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shyla.asmqtt.MessageListener;
import com.shyla.asmqtt.R;
import com.shyla.asmqtt.RemoteControl;
import com.shyla.security.SecurityUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MQTT.Main";

    private RemoteControl mRemoteControl;
    private MainHandler mHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        initView();

        mHandle = new MainHandler();

        mRemoteControl = RemoteControl.createInstance(getApplicationContext());
        mRemoteControl.addListener(mMessageListener);
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
        mRemoteControl.removeListener(mMessageListener);
        RemoteControl.destroyInstance();
        super.onDestroy();
    }

    private MessageListener mMessageListener = new MessageListener() {
        @Override
        public void onMessageArrived(String message) {
            Log.v(TAG, "onMessageArrived, message : " + message);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("info", message);
            msg.setData(bundle);
            mHandle.sendMessage(msg);
        }
    };

    private class MainHandler extends Handler {
        public MainHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String info = bundle.getString("info");
            ((TextView) findViewById(R.id.text_receive_info)).setText("Received info : \n" + info);
        }
    }


    private void initView() {
        findViewById(R.id.btn_lock_door).setOnClickListener(this);
        findViewById(R.id.btn_unlock_door).setOnClickListener(this);
        findViewById(R.id.btn_open_trunk).setOnClickListener(this);

        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_subscribe).setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);
        findViewById(R.id.btn_ssl).setOnClickListener(this);

        findViewById(R.id.btn_parse_bks).setOnClickListener(this);
        findViewById(R.id.btn_parse_keystore).setOnClickListener(this);
        findViewById(R.id.btn_parse_tesla).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lock_door:
                mRemoteControl.publish("lock_door", "control");
                break;
            case R.id.btn_unlock_door:
                mRemoteControl.publish("unlock_door", "control");
                break;
            case R.id.btn_open_trunk:
                mRemoteControl.publish("open_trunk", "control");
                break;

            case R.id.btn_connect:
                mRemoteControl.connect(false);
                break;
            case R.id.btn_subscribe:
                mRemoteControl.subscribe();
                break;
            case R.id.btn_publish:
                mRemoteControl.publish("test message", "control");
                break;
            case R.id.btn_disconnect:
                mRemoteControl.disconnect();
                break;
            case R.id.btn_ssl:
                mRemoteControl.connect(true);
                break;

            case R.id.btn_parse_keystore:
                SecurityUtils.parseKeystore(this);
                break;
            case R.id.btn_parse_bks:
                SecurityUtils.parseBKSCert(this);
                break;
            case R.id.btn_parse_tesla:
                SecurityUtils.parseTeslaKeystore(this);
                break;
        }
    }
}



