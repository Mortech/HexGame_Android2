package com.sam.hex;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class LocalClientListener implements Runnable {
	Thread thread;
	MulticastSocket socket;
	boolean run = true;
	
	public LocalClientListener(MulticastSocket socket) {
		this.socket = socket;
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
        		if(!Global.localObjects.contains(lno)){
        			Global.localObjects.add(lno);
        			Global.adapter.notifyDataSetChanged();
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