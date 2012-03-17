package com.sam.hex.lan;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocalLobbyActivity extends Activity {
	WifiManager wm;
	MulticastLock mcLock;
	WifiBroadcastReceiver broadcastReceiver;
	IntentFilter intentFilter;
	MulticastListener listener;
	MulticastSender sender;
	public static LocalNetworkObject lno;
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
    final Runnable challenger = new Runnable() {
        public void run() {
        	challengeRecieved(LocalLobbyActivity.lno);
        }
    };
    final Runnable startGame = new Runnable() {
        public void run() {
        	Global.localPlayer = LocalLobbyActivity.lno;
        	Global.localPlayer.firstMove = false;
        	HexGame.startNewGame = true;
        	startActivity(new Intent(getBaseContext(),HexGame.class));
        	finish();
        }
    };
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locallobby);
        
        wm = (WifiManager) getSystemService(WIFI_SERVICE);
        mcLock = wm.createMulticastLock("broadcastlock");
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        broadcastReceiver = new WifiBroadcastReceiver(handler, updateResults, challenger, startGame, listener, sender, wm);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//Set player's name
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	Global.player1Name = prefs.getString("player1Name", "Player1");
    	
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
        
        //Allow for broadcasts
        mcLock.acquire();
        
        //Get our ip address
        WifiInfo wifiInfo = wm.getConnectionInfo();
        Global.LANipAddress = String.format("%d.%d.%d.%d",(wifiInfo.getIpAddress() & 0xff),(wifiInfo.getIpAddress() >> 8 & 0xff),(wifiInfo.getIpAddress() >> 16 & 0xff),(wifiInfo.getIpAddress() >> 24 & 0xff));
        
        try {
			//Create a socket
			InetAddress address = InetAddress.getByName("234.235.236.237");
			int port = 4080;
			MulticastSocket socket = new MulticastSocket(port);
			socket.joinGroup(address);
			//(Disables hearing our own voice, off for testing purposes) TODO Turn back on
			//socket.setLoopbackMode(true);
			
			//Create a packet
			String message = ("Let's play Hex. I'm "+Global.player1Name);
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
			
			//Start sending
			sender=new MulticastSender(socket,packet);
			//Start listening
	        listener=new MulticastListener(socket, handler, updateResults, challenger, startGame);
		}
        catch (Exception e) {
			System.out.println(e);
		}
        
        //Listen for connections to a network (Or a disconnection)
        registerReceiver(broadcastReceiver, intentFilter);
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	//Kill our threads
		try{
			sender.stop();
			listener.stop();
		}
		catch(Exception e){}
        mcLock.release();
        unregisterReceiver(broadcastReceiver);
        
        //Clear our cached players from the network
        Global.localObjects = new ArrayList<LocalNetworkObject>();
    }
    
    private void challengeSent(final LocalNetworkObject lno){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	LocalLobbyActivity.lno = lno;
    	        	try{
	    	        	DatagramSocket socket = new DatagramSocket();
	    	        	String message = Global.player1Name+" challenges you.";
	    	        	DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), lno.ip);
	    	        	socket.send(packet);
    	        	}
    	        	catch(Exception e){
    	        		e.getStackTrace();
    	        	}
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
    
    private void challengeRecieved(final LocalNetworkObject lno){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	Global.localPlayer = lno;
    	        	HexGame.startNewGame = true;
    	        	try{
	    	        	DatagramSocket socket = new DatagramSocket();
	    	        	String message = "It's on!";
	    	        	DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), lno.ip);
	    	        	socket.send(packet);
    	        	}
    	        	catch(Exception e){
    	        		e.getStackTrace();
    	        	}
    	        	Global.localPlayer.firstMove = true;
    	        	startActivity(new Intent(getBaseContext(),HexGame.class));
    	        	finish();
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
				challengeSent((LocalNetworkObject) lobby.getItemAtPosition(position));
			}
        });
    }
}