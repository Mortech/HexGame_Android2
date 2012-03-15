package com.sam.hex.lan;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import com.sam.hex.Global;

import android.os.Handler;

public class MulticastListener implements Runnable {
	Thread thread;
	boolean run = true;
	MulticastSocket socket;
	Handler handler;
	Runnable updateUI;
	Runnable challenger;
	
	public MulticastListener(MulticastSocket socket, Handler handler, Runnable updateUI, Runnable challenger) {
		this.socket = socket;
		this.handler = handler;
		this.updateUI = updateUI;
		this.challenger = challenger;
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
	    		InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();        		
        		
	    		if(message.contains("Let's play Hex.")){
	    			String name = message.substring(20);
	        		LocalNetworkObject lno = new LocalNetworkObject(name,address);
	        		if(!Global.localObjects.contains(lno)){/**Removed for the time being for testing conditions TODO Readd this*/ //&& !lno.getIP().equals(Global.LANipAddress)){
	        			Global.localObjects.add(lno);
	        		}
	        		handler.post(updateUI);
	    		}
	    		else if(message.contains("challenges you.")){
	    			for(int i=0;i<Global.localObjects.size();i++){
	        			if(Global.localObjects.get(i).ip.getHostName().equals(address.getHostName())){
	        				LocalLobbyActivity.lno = Global.localObjects.get(i);
	        				handler.post(challenger);
	        				break;
	        			}
	        		}
	    		}
	    		
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	}
	
	public void stop() {
		run = false;
	}
}