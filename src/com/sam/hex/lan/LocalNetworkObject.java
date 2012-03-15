package com.sam.hex.lan;

import java.net.InetSocketAddress;

import android.graphics.Color;

public class LocalNetworkObject{
	String playerName;
	Color playerColor;
	int gridSize;
	InetSocketAddress ip;
	
	public LocalNetworkObject(String name, InetSocketAddress ip) {
		this.playerName = name;
		this.ip = ip;
	}
	
	public LocalNetworkObject(String name, Color playerColor, int gridSize, InetSocketAddress ip) {
		this.playerName = name;
		this.playerColor = playerColor;
		this.gridSize = gridSize;
		this.ip = ip;
	}
	
	public String toString(){
		return playerName;
	}
	
	public boolean equals(Object e){
		if(e instanceof LocalNetworkObject){
			return this.playerName.equals(((LocalNetworkObject) e).playerName) && this.ip.equals(((LocalNetworkObject) e).ip);
		}
		else{
			return false;
		}
	}
	
	public String getIP(){
		return ip.getHostName();
	}
}