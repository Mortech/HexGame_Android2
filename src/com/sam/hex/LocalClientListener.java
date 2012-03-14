package com.sam.hex;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

import android.os.Handler;

public class LocalClientListener implements Runnable {
	Thread thread;
	boolean run = true;
	MulticastSocket socket;
	Handler handler;
	Runnable updateUI;
	
	public LocalClientListener(MulticastSocket socket, Handler handler, Runnable updateUI) {
		this.socket = socket;
		this.handler = handler;
		this.updateUI = updateUI;
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
	    		String message = new String(data, 0 , packet.getLength());
        		System.out.println(message);
        		String[] mes = message.split("(Player: )|(IP Address: )",3);
        		LocalNetworkObject lno = new LocalNetworkObject(mes[1],mes[2]);
        		if(!Global.localObjects.contains(lno)){/**Removed for the time being for testing conditions TODO Readd this*/ //&& !lno.getIP().equals(Global.LANipAddress)){
        			Global.localObjects.add(lno);
        			handler.post(updateUI);
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