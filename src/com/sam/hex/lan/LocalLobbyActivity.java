package com.sam.hex.lan;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LocalLobbyActivity extends Activity {
	WifiManager wm;
	MulticastLock mcLock;
	private LocalClientListener listener = null;
	private LocalClientSender sender = null;
	private List<LocalNetworkObject> players = new ArrayList<LocalNetworkObject>();
    final Handler handler = new Handler();
    final Runnable updateResults = new Runnable() {
        public void run() {
        	if(players!=Global.localObjects){
        		players = Global.localObjects;
        		updateResultsInUi();
        	}
        }
    };
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locallobby);
        
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        mcLock = wm.createMulticastLock("broadcastlock");
    }
    
    @Override
    public void onResume(){
    	super.onResume();

        if (!wm.isWifiEnabled()) {
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	            //Yes button clicked
        	        	wm.setWifiEnabled(true);
        	            break;
        	        case DialogInterface.BUTTON_NEGATIVE:
        	            //No button clicked
        	        	android.os.Process.killProcess(android.os.Process.myPid());
        	            break;
        	        }
        	    }
        	};

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Wifi is off. Enable?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }
        
        mcLock.acquire();
        
        WifiInfo wifiInfo = wm.getConnectionInfo();
        Global.LANipAddress = String.format("%d.%d.%d.%d",(wifiInfo.getIpAddress() & 0xff),(wifiInfo.getIpAddress() >> 8 & 0xff),(wifiInfo.getIpAddress() >> 16 & 0xff),(wifiInfo.getIpAddress() >> 24 & 0xff));
        
		try {
			//Create a socket
			InetAddress address = InetAddress.getByName("234.235.236.237");
			int port = 4080;
			MulticastSocket socket = new MulticastSocket(port);
			socket.joinGroup(address);
			
			//Create a packet
			String message = ("Let's play Hex. Player: "+Global.player1Name+" IP Address: "+Global.LANipAddress);
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
			
			//Start sending
			sender=new LocalClientSender(socket,packet);
			//Start listening
	        listener=new LocalClientListener(socket, handler, updateResults);
	        //TODO Create a listener that runs challengeRecieved() if someone calls us
		}
		catch (Exception e) {
			System.out.println(e);
		}
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	
    	//Kill our threads
		sender.stop();
        listener.stop();
        mcLock.release();
        
        //Clear our cached players from the network
        Global.localObjects = new ArrayList<LocalNetworkObject>();
    }
    
    private void challengeSent(LocalNetworkObject lno){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	//TODO Send challenge to opponent
    	        	startActivity(new Intent(getBaseContext(),HexGame.class));
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	        	//Do nothing
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Do you want to challenge "+lno.toString()+"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
    
    private void challengeRecieved(LocalNetworkObject lno){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	startActivity(new Intent(getBaseContext(),HexGame.class));
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	        	//Do nothing
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(lno.toString()+" challenges you. Accept?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
    
    private void updateResultsInUi(){
    	final ListView lobby = (ListView) findViewById(R.id.players);
        ArrayAdapter<LocalNetworkObject> adapter = new ArrayAdapter<LocalNetworkObject>(this,android.R.layout.simple_list_item_1, Global.localObjects);
        lobby.setAdapter(adapter);
        
        lobby.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "IP Address: " + ((LocalNetworkObject) lobby.getItemAtPosition(position)).getIP(), Toast.LENGTH_LONG).show();
				challengeSent((LocalNetworkObject) lobby.getItemAtPosition(position));
			}
        });
    }
}