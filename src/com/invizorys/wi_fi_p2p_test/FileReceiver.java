package com.invizorys.wi_fi_p2p_test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class FileReceiver extends AsyncTask<Void, Void, String> {
	private Context mContext;

	public FileReceiver(Context context) {
		mContext = context;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			ServerSocket serverSocket = new ServerSocket(Util.PORT);
			Log.d(MainActivity.TAG, "Server: Socket opened");
			Socket client = serverSocket.accept();
			Log.d(MainActivity.TAG, "Server: connection done");

			final File file = new File(Environment.getExternalStorageDirectory()
					+ "/" + mContext.getPackageName() + "/wifip2pshared-"
					+ System.currentTimeMillis() + ".txt");
			
			File dirs = new File(file.getParent());
			if (!dirs.exists())
				dirs.mkdirs();
			file.createNewFile();
			
			Log.d(MainActivity.TAG, "copying files " + file.toString());
            InputStream inputstream = client.getInputStream();
            Util.copyFile(inputstream, new FileOutputStream(file));
            serverSocket.close();
            return file.getAbsolutePath();
            
		} catch (Exception e) {
			Log.e(MainActivity.TAG, e.getMessage());
            return null;
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
//            statusText.setText("File copied - " + result);
			Toast.makeText(mContext, "File copied - " + result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".txt");

            intent.setDataAndType(Uri.parse("file://" + result), "text/*");
            mContext.startActivity(intent);
        }
	}

}
