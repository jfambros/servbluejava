package com.example.blue1;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ServidorBlue extends Thread {
/**
* Tag that will appear in the log.
*/
private final String ACCEPT_TAG = ServidorBlue.class.getName();
/**
* The bluetooth server socket.
*/
	private final BluetoothServerSocket mServerSocket;

	public ServidorBlue(BluetoothAdapter ba, UUID uuid) {
		BluetoothServerSocket tmp = null;
		try {
		tmp = ba.listenUsingRfcommWithServiceRecord(ACCEPT_TAG, uuid);
		} catch (IOException e) {
		e.printStackTrace();
		}
		mServerSocket = tmp;
	}
	public void run() {
	BluetoothSocket socket = null;
	while (true) {
	try {
	Log.i(ACCEPT_TAG, "Listening for a connection...");
	socket = mServerSocket.accept();
	Log.i(ACCEPT_TAG, "Connected to " + socket.getRemoteDevice().getName());
	} catch (IOException e) {
		Log.e("Error Con", e.toString());
	break;
	}
	// If a connection was accepted
	if (socket != null) {
	// Do work to manage the connection (in a separate thread)
	try {
	// Read the incoming string.
	String buffer;
	DataInputStream in = new DataInputStream(socket.getInputStream());
	buffer = in.readUTF();
	Log.i("dato de", buffer+socket.getRemoteDevice().getName()+" ");
	
	} catch (IOException e) {
	Log.e(ACCEPT_TAG, "Error obtaining InputStream from socket");
	e.printStackTrace();
	}
	try {
	mServerSocket.close();
	} catch (IOException e) { }
	break;
	}
	}
	}
	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
	try {
	mServerSocket.close();
	} catch (IOException e) { }
	}
}