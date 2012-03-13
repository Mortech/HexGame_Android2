package com.sam.hex;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class LocalClientSender implements Runnable {
	Thread thread;
	MulticastSocket socket;
	DatagramPacket packet;

	public LocalClientSender(MulticastSocket socket, DatagramPacket packet) {
		this.socket = socket;
		this.packet = packet;
		thread = new Thread(this, "LANshout"); //Create a new thread.
		thread.start(); //Start the thread.
	}
	
	public void run() {
		while(true){
        	try {
        		socket.send(packet);
        		System.out.println("Sending...");
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
}