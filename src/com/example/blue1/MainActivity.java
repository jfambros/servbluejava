package com.example.blue1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BluetoothAdapter ba;
    protected static final String TAG = "BLUETOOTH";
	private static final int BLUETOOTH_ACT = 1;
	protected static final int SOLICITADESCUBRIMIENTO = 1;	
	private Button bActivar;
	private Button bBuscar;
	private Button bEnviar;
	private EditText txtMsg;
	private TextView texto;
	private ListView listaDisp;
	//ArrayList de dispositivos
	private ArrayList<BluetoothDevice> alBD = new ArrayList<BluetoothDevice>();
	private ArrayList<String> arraylBDString = new ArrayList<String>();
	
	private BluetoothSocket transferSocket;
	private static final UUID MIUDDI = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private String mac = "00:11:67:89:3F:EF";
	
	private ServidorBlue servB;
	


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bActivar = (Button)findViewById(R.id.bActivar);
		bActivar.setOnClickListener(bActivarP);
		bBuscar = (Button)findViewById(R.id.bBuscar);
		bBuscar.setOnClickListener(bBuscarP);
		bEnviar = (Button)findViewById(R.id.bEnviar);
		bEnviar.setOnClickListener(bEnviarP);
		
		
		texto = (TextView)findViewById(R.id.tEstado);
		listaDisp = (ListView)findViewById(R.id.lDisp);
		

		
		ba = BluetoothAdapter.getDefaultAdapter();
		if (ba==null){
			Toast.makeText(getApplicationContext(), "No soportado", Toast.LENGTH_LONG).show();
		}
		else{
			String dir = ba.getAddress();
			String nombre = ba.getName();
		}
		
		listaDisp.setOnItemClickListener(listaP);
		
		
	}
	
	private OnClickListener bEnviarP = new OnClickListener() {
		public void onClick(View v) {
			enviaMsg();		
		}
	};
	private OnItemClickListener listaP = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			conectaServBlue(pos);
			
		}
	};

	private OnClickListener bActivarP = new OnClickListener() {
		public void onClick(View v) {
			iniBluetooth();

		}
	};
	
	private OnClickListener bBuscarP = new OnClickListener() {
		public void onClick(View v) {
			iniciarDescub();

		}
	};
	
	private void enviaMsg(){
		 //enviar, prueba
        OutputStream os;
		try {
			txtMsg = (EditText)findViewById(R.id.txtMsg);
			
			os = transferSocket.getOutputStream();
	        String msg = txtMsg.getText().toString()+"\n";
	        byte[] buff = msg.getBytes();
	        os.write(buff);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void conectaServBlue(int pos){
		try{
			
			Log.i("Device",alBD.get(pos).getAddress());
			BluetoothDevice bd = alBD.get(pos);
			
			SystemClock.sleep(1000);
			ba.cancelDiscovery();
			SystemClock.sleep(1000);
			//socket.connect();
			
			
	        BluetoothSocket clientSocket 
	          = bd.createRfcommSocketToServiceRecord(MIUDDI);
	        Log.i("Conecta", "Cliente conec ...");
	        // Block until server connection accepted.
	        clientSocket.connect();
	        Log.i("Conecta", "Conectando..");
       
	        LeerMsg leer = new LeerMsg(clientSocket);
	        leer.start();

	        transferSocket = clientSocket;
	        Log.i("Conecta", "Enlazando.. .");
	        servB = new ServidorBlue(ba, MIUDDI);
	        servB.start();

	      } catch (Exception e) {
	        Log.e("BLUETOOTH ", "Blueooth client I/O Exception", e);
	      }
	}
	private void iniBluetooth() {
	      if (!ba.isEnabled()) { 
	        // Bluetooth isn't enabled, prompt the user to turn it on.
	        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(intent, BLUETOOTH_ACT);
	      } else {
	        // Bluetooth is enabled, initialize the UI.
	        //initBluetoothUI();
	    	  iniciaGui();
	    	  
	      }
	    }
	
	private class LeerMsg extends Thread{
		private BluetoothSocket bs;
	
		public LeerMsg(BluetoothSocket bs){
			this.bs = bs;
		}
		public void run() {
			try{
				while(true){
					
			        InputStream is = bs.getInputStream();
			        BufferedReader bReader=new BufferedReader(new InputStreamReader(is));
			        String lineRead=bReader.readLine();
			        Log.i("recibido",lineRead);

				}		        
			}
			catch(Exception err){
				Log.e("Error leer", err.toString());
			}
		}
	}
	
	private void iniciarDescub() {
		Log.i("iniciando","buscando");
		alBD.clear();
	      registerReceiver(brDescubrir,
	                       new IntentFilter(BluetoothDevice.ACTION_FOUND));

	      if (ba.isEnabled() && !ba.isDiscovering())
	        alBD.clear();
	      
	      ba.startDiscovery();
	    }
	
	private void iniciaGui(){
        texto.setText("Activado Bluetooth");
	}
	
	private final BroadcastReceiver brDescubrir= new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String remoteDeviceName = 
			          intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

			        BluetoothDevice remoteDevice =  
			          intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			        alBD.add(remoteDevice);

			        Log.d(TAG, "Discovered " + remoteDeviceName);
			        if (remoteDeviceName!=null){
			        	arraylBDString.add(remoteDeviceName);
				        ArrayAdapter<String> aaDisp = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,arraylBDString);
				        listaDisp.setAdapter(aaDisp);
			        }
			
		}
	};

	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BLUETOOTH_ACT)
	        if (resultCode == RESULT_OK) {
	          // Bluetooth has been enabled, initialize the UI.
	          iniciaGui();
	        }
	        else{
	        	Toast.makeText(getApplicationContext(), "No activado", Toast.LENGTH_LONG).show();
	        }
		
	      
	      /**
	       * Listing 16-4: Monitoring discoverability request approval
	       */
	      if (requestCode == SOLICITADESCUBRIMIENTO) {
	        if (resultCode == RESULT_CANCELED) {
	          Log.d(TAG, "Discovery cancelled by user");
	        }
	      }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
