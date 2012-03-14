package com.sam.hex;

public class LocalNetworkObject{
	String playerName;
	String ip;
	
	public LocalNetworkObject(String name, String ip) {
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
		return ip;
	}
}