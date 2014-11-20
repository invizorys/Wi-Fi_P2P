package com.invizorys.wi_fi_p2p_test;

import java.util.List;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
	private List<WifiP2pDevice> items;
	private Context mContext;

	public WiFiPeerListAdapter(Context context, int textViewResourceId,
			List<WifiP2pDevice> objects) {
		super(context, textViewResourceId, objects);
		items = objects;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);
		}
		WifiP2pDevice device = items.get(position);
		if (device != null) {
			TextView top = (TextView) v.findViewById(R.id.device_name_textview);
			TextView bottom = (TextView) v.findViewById(R.id.device_details_textview);
			if (top != null) {
				top.setText(device.deviceName);
			}
			if (bottom != null) {
				bottom.setText(Util.getDeviceStatus(device.status));
			}
		}
		return v;
	}
}
