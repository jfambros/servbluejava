package com.example.blue1;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	private TextView texto;
	private ListView listaDisp;
	//ArrayList de dispositivos
	private ArrayList<BluetoothDevice> alBD = new ArrayList<BluetoothDevice>();
	private ArrayList<String> arraylBDString = new ArrayList<String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bActivar = (Button)findViewById(R.id.bActivar);
		bActivar.setOnClickListener(bActivarP);
		bBuscar = (Button)findViewById(R.id.bBuscar);
		bBuscar.setOnClickListener(bBuscarP);
		
		
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
			        arraylBDString.add(remoteDeviceName);
			        ArrayAdapter<String> aaDisp = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,arraylBDString);
			        listaDisp.setAdapter(aaDisp);
			
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
