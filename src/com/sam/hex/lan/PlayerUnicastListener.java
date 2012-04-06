package com.sam.hex.lan;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.R;

public class PlayerUnicastListener implements Runnable {
	Thread thread;
	boolean run = true;
	DatagramSocket socket;
	SharedPreferences prefs;
	int team;
	
	public PlayerUnicastListener(int team) {
		this.team = team;
		try {
			this.socket = new DatagramSocket(LANGlobal.playerPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(Global.board.getContext());
		thread = new Thread(this, "LANscan"); //Create a new thread.
		thread.start(); //Start the thread.
	}
	
	public void run() {
		//Listen for other players
		byte[] data = new byte[1024];
	    while(run)
	    {
	    	try {
	    		DatagramPacket packet = new DatagramPacket(data, data.length);
	    		socket.receive(packet);
	    		String message = new String(data, 0, packet.getLength());
	    		InetAddress address = packet.getAddress();
        		
	    		if(!LANGlobal.localPlayer.ip.equals(address)) continue;
	    		
	    		System.out.println(message);
	    		if(message.contains("Move: ")){
	    			int x = Integer.decode(message.substring(6,message.indexOf(",")));
					int y = Integer.decode(message.substring(message.indexOf(",")+1));
					LANGlobal.hex = new Point(x,y);
	    		}
	    		else if(message.contains("I changed my color to ")){
	    			//Full message looks like: I changed my color to _color_
	    			LANGlobal.localPlayer.playerColor = Integer.decode(message.substring(22));
	    			HexGame.setColors(prefs);
	    			Global.board.postInvalidate();
	    		}
	    		else if(message.contains("I changed my name to ")){
	    			//Full message looks like: I changed my name to _name_
	    			LANGlobal.localPlayer.playerName = message.substring(21);
	    			HexGame.setNames(prefs);
	    			Global.board.postInvalidate();
	    		}
	    		else if(message.equals("I win!")){
	    			
	    		}
	    		else if(message.equals("Cheater.")){
	    			
	    		}
	    		else if(message.equals("Want to play a new game?")){
	    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	        switch (which){
	    	    	        case DialogInterface.BUTTON_POSITIVE:
	    	    	            //Yes button clicked
	    	    	        	HexGame.startNewGame=true;
	    	    	        	new LANMessage("Sure, let's play again", LANGlobal.localPlayer.ip, LANGlobal.playerPort);
	    	    	            break;
	    	    	        case DialogInterface.BUTTON_NEGATIVE:
	    	    	            //No button clicked
	    	    	        	new LANMessage("No, I don't want to play again", LANGlobal.localPlayer.ip, LANGlobal.playerPort);
	    	    	            break;
	    	    	        }
	    	    	    }
	    	    	};

	    	    	AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(LANGlobal.localPlayer.playerName+" "+Global.board.getContext().getString(R.string.newLANGame)).setPositiveButton(Global.board.getContext().getString(R.string.yes), dialogClickListener).setNegativeButton(Global.board.getContext().getString(R.string.no), dialogClickListener).show();
	    		}
	    		else if(message.equals("Sure, let's play again")){
	    			HexGame.startNewGame=true;
	    	    	AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(Global.board.getContext().getString(R.string.LANplayAgain)).setNeutralButton(Global.board.getContext().getString(R.string.okay), null).show();
	    		}
	    		else if(message.equals("No, I don't want to play again")){
	    			AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(Global.board.getContext().getString(R.string.LANdontPlayAgain)).setNeutralButton(Global.board.getContext().getString(R.string.okay), null).show();
	    		}
	    		else if(message.equals("Can I undo?")){
	    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	        switch (which){
	    	    	        case DialogInterface.BUTTON_POSITIVE:
	    	    	            //Yes button clicked
	    	    	        	GameAction.undo();
	    	    	        	new LANMessage("Sure, undo", LANGlobal.localPlayer.ip, LANGlobal.playerPort);
	    	    	            break;
	    	    	        case DialogInterface.BUTTON_NEGATIVE:
	    	    	            //No button clicked
	    	    	        	new LANMessage("No, you cannot undo", LANGlobal.localPlayer.ip, LANGlobal.playerPort);
	    	    	            break;
	    	    	        }
	    	    	    }
	    	    	};

	    	    	AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(LANGlobal.localPlayer.playerName+" "+Global.board.getContext().getString(R.string.LANUndo)).setPositiveButton(Global.board.getContext().getString(R.string.yes), dialogClickListener).setNegativeButton(Global.board.getContext().getString(R.string.no), dialogClickListener).show();
	    		}
	    		else if(message.equals("Sure, undo")){
	    			GameAction.undo();
	    			AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(Global.board.getContext().getString(R.string.LANundoAccepted)).setNeutralButton(Global.board.getContext().getString(R.string.okay), null).show();
	    		}
	    		else if(message.equals("No, you cannot undo")){
	    			AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
	    	    	builder.setMessage(Global.board.getContext().getString(R.string.LANundoDenied)).setNeutralButton(Global.board.getContext().getString(R.string.okay), null).show();
	    		}
	    		
			}
	    	catch (Exception e) {
				e.printStackTrace();
			}
	    }

	}
	
	public void stop() {
		run = false;
	}
}