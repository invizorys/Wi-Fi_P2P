package com.invizorys.wi_fi_p2p_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.invizorys.wi_fi_p2p_test.R;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private MainActivity mActivity;
	private PeerListListener mPeerListListener;
	
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, MainActivity activity) {
		mManager = manager;
		mChannel = channel;
		mActivity = activity;
		mPeerListListener = (PeerListListener)mActivity.getFragmentManager()
				.findFragmentById(R.id.devicelist);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Determine if Wifi P2P mode is enabled or not, alert the Activity.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				mActivity.setIsWifiP2pEnabled(true);
			} else {
				mActivity.setIsWifiP2pEnabled(false);
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			// Request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (mManager != null) {
				mManager.requestPeers(mChannel, mPeerListListener);
			}
			Log.d(MainActivity.TAG, "P2P peers changed");

		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkState.isConnected()) {
				mManager.requestConnectionInfo(mChannel, (ConnectionInfoListener) mActivity);
			} else {
				mManager.cancelConnect(mChannel, null);
			}

		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {
//			DeviceListFragment fragment = (DeviceListFragment) mActivity
//					.getFragmentManager().findFragmentById(R.id.devicelist);
			mActivity.updateThisDevice((WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
		}
	}

}
