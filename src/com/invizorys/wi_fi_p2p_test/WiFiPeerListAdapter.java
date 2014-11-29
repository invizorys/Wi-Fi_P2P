package com.invizorys.wi_fi_p2p_test;

import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
	private List<WifiP2pDevice> items;
	private MainActivity mActivity;

	public WiFiPeerListAdapter(MainActivity context, int textViewResourceId,
			List<WifiP2pDevice> objects) {
		super(context, textViewResourceId, objects);
		items = objects;
		mActivity = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);
		}
		final WifiP2pDevice device = items.get(position);
		if (device != null) {
			
			TextView top = (TextView) v.findViewById(R.id.device_name_textview);
			TextView bottom = (TextView) v.findViewById(R.id.device_details_textview);
			if (top != null) {
				top.setText(device.deviceName);
			}
			if (bottom != null) {
				bottom.setText(Util.getDeviceStatus(device.status));
			}
			if (device.isGroupOwner()) {
				Button connectBtn = (Button) v.findViewById(R.id.connect_button);
				connectBtn.setVisibility(View.VISIBLE);
				connectBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.connect(device);
					}
				});
				Button disconnectBtn = (Button) v.findViewById(R.id.disconnect_button);
				disconnectBtn.setVisibility(View.VISIBLE);
				disconnectBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.disconnect();
					}
				});
			}
		}
		return v;
	}
}
