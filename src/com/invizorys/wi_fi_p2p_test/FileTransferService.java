package com.invizorys.wi_fi_p2p_test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "send_file";
	public static final String GROUP_OWNER_ADDRESS = "host";
	public static final String GROUP_OWNER_PORT = "port";
	public static final String FILE_PATH = "file_url";

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context mContext = getApplicationContext();

		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String host = intent.getExtras().getString(GROUP_OWNER_ADDRESS);
			int port = intent.getExtras().getInt(GROUP_OWNER_PORT);
			Socket socket = new Socket();
			String fileUri = intent.getExtras().getString(FILE_PATH);

			try {
				Log.d(MainActivity.TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
				Log.d(MainActivity.TAG, "Client socket - " + socket.isConnected());

				OutputStream outputStream = socket.getOutputStream();
				ContentResolver cr = mContext.getContentResolver();
				InputStream inputStream = null;

				try {
					inputStream = cr.openInputStream(Uri.parse(fileUri));
				} catch (FileNotFoundException e) {
					Log.d(MainActivity.TAG, e.toString());
				}

				Util.copyFile(inputStream, outputStream);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {

				if (socket != null) {
					if (socket.isConnected())
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}

			}
		}
	}

}
