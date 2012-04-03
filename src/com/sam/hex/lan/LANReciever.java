package com.sam.hex.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class LANReciever{
	public LANReciever(String message, InetAddress ip, int port, MessageRunnable run) {
		byte[] data = new byte[1024];
		try {
			DatagramSocket socket = new DatagramSocket(LANGlobal.port);
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			String packetMessage = new String(data, 0, packet.getLength());
			System.out.println(packetMessage);
			while(!packetMessage.contains(message) || !packet.getAddress().equals(ip)){
				socket.receive(packet);
			}
			run.message = packetMessage;
			run.run();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}