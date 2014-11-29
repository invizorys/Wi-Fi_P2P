package com.invizorys.wi_fi_p2p_test;

import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Bundle;

public class MainActivity extends Activity implements OnClickListener, ConnectionInfoListener {

	public static final String TAG = "wifidirect";

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel mChannel;
	private WifiP2pManager mManager;
	private BroadcastReceiver mReceiver;
	private boolean isWifiP2pEnabled = false;
	private Context mContext = this;
	private int CHOOSE_FILE_RESULT_CODE = 20;
	private WifiP2pInfo mInfo;
	
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
		
		findViewById(R.id.search_button).setOnClickListener(this);
		findViewById(R.id.create_group_button).setOnClickListener(this);
		findViewById(R.id.remove_group_button).setOnClickListener(this);
		findViewById(R.id.choose_file_button).setOnClickListener(this);
		findViewById(R.id.restart_wifi_button).setOnClickListener(this);
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

	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	private void searchPears() {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				showMessage("success pears search");
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast.makeText(mContext, "isWifiP2pEnabled: " + isWifiP2pEnabled
								+ "\n search fail: " + reasonCode, Toast.LENGTH_SHORT).show();
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

	private void createGroup() {
		removeGroup();
		mManager.createGroup(mChannel, new ActionListener() {
			
			@Override
			public void onSuccess() {
				Timer timer = new Timer();
				TimerTask timerTask = new TimerTask() {
					
					@Override
					public void run() {
						requestGroupInfo();
					}
				};
				timer.schedule(timerTask, 2000);
			}
			
			@Override
			public void onFailure(int reason) {
				Toast.makeText(mContext, "Group creating failure: " + reason, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void removeGroup() {
		mManager.removeGroup(mChannel, new ActionListener() {

			@Override
			public void onSuccess() {
				showMessage("success remove group");
			}

			@Override
			public void onFailure(int reason) {
				showMessage("failure remove group: " + reason);
			}
		});
	}
	
	private void requestGroupInfo()
	{
		mManager.requestGroupInfo(mChannel, new GroupInfoListener() {
			
			@Override
			public void onGroupInfoAvailable(WifiP2pGroup group) {
				if (group != null)
					Toast.makeText(mContext, "Group Passphrase: " + group.getNetworkName() + 
							" Group Passphrase: " + group.getPassphrase(), Toast.LENGTH_LONG).show();
				else
					Toast.makeText(mContext, "Group is null ", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void connect(WifiP2pDevice device) {

		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;

		mManager.connect(mChannel, config, new ActionListener() {
			
			@Override
			public void onSuccess() {
				showMessage("success connect");
			}
			
			@Override
			public void onFailure(int reason) {
				showMessage("failure connect: " + reason);
			}
		});
	}
	
	public void disconnect() {
		mManager.removeGroup(mChannel, new ActionListener() {
			
			@Override
			public void onSuccess() {
				showMessage("Disconnected.");
			}
			
			@Override
			public void onFailure(int reason) {
				showMessage("Disconnect failed. Reason :" + reason);
			}
		});
	}
	
	private void restartWifi()
	{
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		wifiManager.setWifiEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_button:
			searchPears();
			break;
			
		case R.id.create_group_button:
			createGroup();
			break;
			
		case R.id.remove_group_button:
			removeGroup();
			break;
			
		case R.id.choose_file_button:
			if (mInfo != null && !mInfo.isGroupOwner && mInfo.groupFormed) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/*");
				startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
			}
			break;
			
		case R.id.restart_wifi_button:
			restartWifi();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mInfo != null) {
			Uri uri = data.getData();
			showMessage("Sending...");
			Intent serviceIntent = new Intent(mContext, FileTransferService.class);
			serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
			serviceIntent.putExtra(FileTransferService.FILE_PATH, uri.toString());
			serviceIntent.putExtra(FileTransferService.GROUP_OWNER_ADDRESS, mInfo.groupOwnerAddress.getHostAddress());
			serviceIntent.putExtra(FileTransferService.GROUP_OWNER_PORT, Util.PORT);
			mContext.startService(serviceIntent);
		}
		else {
			if(resultCode != 0)
				showMessage("Wi-Fi p2p group not available");
		}
	}
	
	private void showMessage(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		mInfo = info;

		if (info.groupFormed && info.isGroupOwner) {
			new FileReceiver(mContext).execute();
		}
	}
	
}
