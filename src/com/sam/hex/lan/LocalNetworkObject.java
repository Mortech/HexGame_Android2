package com.sam.hex.lan;

import java.net.InetSocketAddress;

import com.sam.hex.Global;

public class LocalNetworkObject{
	String playerName;
	int playerColor=Global.player2DefaultColor;
	int gridSize = 0;
	InetSocketAddress ip;
	boolean firstMove = false;
	
	public LocalNetworkObject(String name, InetSocketAddress ip) {
		this.playerName = name;
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