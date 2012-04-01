package com.sam.hex.lan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.graphics.Point;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.Move;
import com.sam.hex.PlayingEntity;

public class LocalPlayerObject implements PlayingEntity {
	byte[][] gameBoard; 
	byte team;
	UnicastListener listener;
	
	public LocalPlayerObject(byte team) {
		this.team=team;//Set the player's team
		listener = new UnicastListener();
	}
	
	public void getPlayerTurn() {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    	ObjectOutputStream oStream;
		try {
			oStream = new ObjectOutputStream(bStream);
			oStream.writeObject(Global.moveList.getmove());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = bStream.toByteArray();
		new LANMessage(data, LANGlobal.localPlayer.ip, LANGlobal.port);
		
		DatagramSocket socket;
		byte[] recievedData = data;
		Move move = null;
		try {
			socket = new DatagramSocket(LANGlobal.port);
			DatagramPacket packet = new DatagramPacket(recievedData, recievedData.length);
			socket.receive(packet);
			move = (Move) new ObjectInputStream(new ByteArrayInputStream(recievedData)).readObject();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Point myMove = new Point(move.getX(), move.getY());
		System.out.println("Move: "+myMove.x+","+myMove.y);
		GameAction.makeMove(this, team, myMove);
	}
	
	public void undoCalled(){
	}

	@Override
	public void newgameCalled() {
	}
	
	@Override
	public boolean supportsUndo() {
		return false;
	}

	@Override
	public boolean supportsNewgame() {
		return false;
	}
}