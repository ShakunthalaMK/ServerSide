package cos.bluetooth.serverside;

import android.os.Bundle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ServerActivity extends Activity {

	TextView displayText;
	public static final String TAG = "ServerSide";
	
	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Log.e(TAG, "============Successfull=========");
			msg.obtain();

			displayText.setText(msg.obj + "");
			// }

			super.handleMessage(msg);
		}
	};

	final Runnable updateUI = new Runnable() {
		public void run() {

		}
	};

	BluetoothServer bluetoothServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);

		displayText = (TextView) findViewById(R.id.display_text);
		bluetoothServer = new BluetoothServer(handler, updateUI);
		bluetoothServer.start();

	}
}

class BluetoothServer extends Thread {
	public static final String TAG = "BluetoothServer";
	BluetoothAdapter mBluetoothAdapter = null;
	double data;

	final Handler mUIhandler;
	final Runnable updateUI;

	public BluetoothServer(Handler handler, Runnable updateUI) {
		this.mUIhandler = handler;
		this.updateUI = updateUI;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public double getBluetoothServer() {
		return data;
	}

	public void run() {
		BluetoothServerSocket serverSocket = null ;
		BluetoothSocket socket = null;
		try {
			serverSocket = mBluetoothAdapter
					.listenUsingRfcommWithServiceRecord("helloService", UUID
							.fromString("00001101-0000-1000-8000-00805F9B34FB"));

			socket = serverSocket.accept(); // block for connect

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			
		//This is to read serial data continuously from client and display in UI.
			while(true)
			{
				Message msg = mUIhandler.obtainMessage();
				data = in.readDouble(); // Read from client
				msg.obj = data;
	
				Log.e(TAG, "" + data);
				mUIhandler.sendMessage(msg);
				
			}
			
		} catch (Exception e) {
		}
		// Closing the socket
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
