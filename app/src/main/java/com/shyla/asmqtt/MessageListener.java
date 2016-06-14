package com.shyla.asmqtt;

/**
 * Created by xiaxing on 16-6-14.
 */
public interface MessageListener {
    void onMessageArrived(String message);
}
