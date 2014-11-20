package com.invizorys.wi_fi_p2p_test;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

	public static final String TAG = "wifidirect";

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel mChannel;
	private WifiP2pManager mManager;
	private BroadcastReceiver mReceiver;
	private boolean isWifiP2pEnabled = false;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		
		setOnClickListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private void setOnClickListeners() {
		findViewById(R.id.search_button).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						searchPears();
					}
				});
	}

	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	private void searchPears() {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() { }

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(mContext, "search fail: " + reasonCode, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void updateThisDevice(WifiP2pDevice device) {
		TextView view = (TextView) findViewById(R.id.mystatus_textview);
		view.setText("My Name: " + device.deviceName + "\nMy Address: "
				+ device.deviceAddress + "\nMy Status: "
				+ Util.getDeviceStatus(device.status));
		return;
	}
}
