package com.example.blue1;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
		
		/*
		blueAdap = BluetoothAdapter.getDefaultAdapter();
		
		if(blueAdap == null) {
	    	  onBtn.setEnabled(false);
	    	  offBtn.setEnabled(false);
	    	  listBtn.setEnabled(false);
	    	  buscaBtn.setEnabled(false);
	    	  text.setText("Status: no soportado");
	    	  
	    	  Toast.makeText(getApplicationContext(),"No hay soporte bluetooth",
	         		 Toast.LENGTH_LONG).show();
	    }
		else{
			if (blueAdap.isEnabled()){
				text.setText("Soportado");
			}
		}
		
		*/
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
			os = transferSocket.getOutputStream();
	        String msg = "Hola!!!\n";
	        byte[] buff = msg.getBytes();
	        os.write(buff);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void conectaServBlue(int pos){
		try{
			ba.cancelDiscovery();
			Log.i("Device",alBD.get(pos).getAddress());
			BluetoothDevice bd = alBD.get(pos);
			
	        BluetoothSocket clientSocket 
	          = bd.createRfcommSocketToServiceRecord(MIUDDI);
	        Log.i("Conecta", "Cliente...");
	        // Block until server connection accepted.
	        clientSocket.connect();
	        Log.i("Conecta", "Conectando...");
	        // Start listening for messages.
	        StringBuilder incoming = new StringBuilder();
	        //listenForMessages(clientSocket, incoming);

	        // Add a reference to the socket used to send messages.
	        transferSocket = clientSocket;

	      } catch (IOException e) {
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
	
	private void iniciarDescub() {
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
	
	private void descubreDisp(){
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BLUETOOTH_ACT)
	        if (resultCode == RESULT_OK) {
	          // Bluetooth has been enabled, initialize the UI.
	          iniciaGui();
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
