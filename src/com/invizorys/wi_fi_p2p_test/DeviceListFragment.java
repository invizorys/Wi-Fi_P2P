package com.invizorys.wi_fi_p2p_test;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;

public class DeviceListFragment extends ListFragment implements
		PeerListListener {

	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private ProgressDialog progressDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.list_item, peers));
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
		peers.clear();
		peers.addAll(peerList.getDeviceList());

		// If an AdapterView is backed by this data, notify it
		// of the change. For instance, if you have a ListView of available
		// peers, trigger an update.
		((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if (peers.size() == 0) {
			Log.d(MainActivity.TAG, "No devices found");
			return;
		}
	}

}
