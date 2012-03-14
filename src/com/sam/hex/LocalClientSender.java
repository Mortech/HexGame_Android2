package com.sam.hex;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class LocalClientSender implements Runnable {
	Thread thread;
	MulticastSocket socket;
	DatagramPacket packet;
	boolean run = true;

	public LocalClientSender(MulticastSocket socket, DatagramPacket packet) {
		this.socket = socket;
		this.packet = packet;
		thread = new Thread(this, "LANshout"); //Create a new thread.
		thread.start(); //Start the thread.
	}
	
	public void run() {
		while(run){
        	try {
        		socket.send(packet);
        		System.out.println("Sending...");
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	public void stop() {
		run = false;
	}
}