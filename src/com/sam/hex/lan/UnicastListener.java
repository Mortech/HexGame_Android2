package com.sam.hex.lan;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.sam.hex.Global;

import android.os.Handler;

public class UnicastListener implements Runnable {
	Thread thread;
	boolean run = true;
	DatagramSocket socket;
	Handler handler;
	Runnable challenger;
	Runnable startGame;
	
	public UnicastListener(Handler handler, Runnable challenger, Runnable startGame) {
		try {
			this.socket = new DatagramSocket(LANGlobal.challengerPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.handler = handler;
		this.challenger = challenger;
		this.startGame = startGame;
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
	    		System.out.println(message);
	    		InetAddress address = packet.getAddress();        		
        		
	    		if(message.contains("challenges you. Grid size: ")){
	    			boolean flag = true;
	    			for(int i=0;i<LANGlobal.localObjects.size();i++){
	        			if(LANGlobal.localObjects.get(i).ip.equals(address)){
	        				flag = false;
	        				LocalLobbyActivity.lno = LANGlobal.localObjects.get(i);
	        				LocalLobbyActivity.lno.firstMove = true;
	        				//Full message looks like: _playername_ challenges you. Grid size: _gridsize_
	        				LocalLobbyActivity.lno.gridSize = Integer.decode(message.substring(message.lastIndexOf("Grid size: ")+11));//Grab the grid size from the end of the message
	        				handler.post(challenger);
	        				break;
	        			}
	        		}
	    			if(flag){
	    				LocalLobbyActivity.lno.playerName = message.substring(0, message.lastIndexOf(" challenges you"));
	    				LocalLobbyActivity.lno.firstMove = true;
	    				LocalLobbyActivity.lno.gridSize = Integer.decode(message.substring(message.lastIndexOf("Grid size: ")+11));
	    				LocalLobbyActivity.lno.ip = address;
	    				handler.post(challenger);
	    			}
	    		}
	    		else if(message.contains("It's on! My color's ") && LocalLobbyActivity.lno.ip.equals(address)){
	    			//Full message looks like: It's on! My color's _playercolor_
	    			LocalLobbyActivity.lno.playerColor = Integer.parseInt(message.substring(20));//Grab the color from the end of the message
	    			
	    			//Send our color over
	    			new LANMessage("My color is "+Global.player1Color, LocalLobbyActivity.lno.ip, LANGlobal.challengerPort);
	    			
	    			handler.post(startGame);
    				break;
	    		}
	    		else if(message.contains("My color is ") && LocalLobbyActivity.lno.ip.equals(address)){
	    			//Full message looks like: My color is _playercolor_
	    			LANGlobal.localPlayer.playerColor = Integer.parseInt(message.substring(12));//Grab the color from the end of the message
	    			
	    			handler.post(startGame);
	    			break;
	    		}
			}
	    	catch (Exception e) {
				System.out.println(e);
			}
	    }
	}
	
	public void stop() {
		run = false;
	}
}