package com.monkey.entonado;

import java.util.Collection;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;

public class WiFiDirectBroadCastReceiver extends BroadcastReceiver 
{
	//-------------------------------
	//Atributos
	//-------------------------------

    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;

    //--------------------------------
    //Constructor
    //--------------------------------
    /**
     * Crea el broadcast reciever
     * @param manager
     * @param channel
     * @param activity
     */
    public WiFiDirectBroadCastReceiver(WifiP2pManager manager, Channel channel,
    		MainActivity activity) 
    {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        String s =  intent.getDataString();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) 
        {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.setIsWifiP2pEnabled(true);
            } else {
                mActivity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) 
        {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	// request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) 
        {
            
        }

    }
    
    
}

