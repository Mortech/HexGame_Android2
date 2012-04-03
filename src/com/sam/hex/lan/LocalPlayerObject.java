package com.sam.hex.lan;

import android.graphics.Point;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
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
		new LANMessage("Move: "+Global.moveList.getmove().getX()+","+Global.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.port);
		
		new LANReciever("Move: ", LANGlobal.localPlayer.ip, LANGlobal.port, new MessageRunnable(){
			@Override
			public void run() {
				int x = Integer.decode(message.substring(message.indexOf(": "),message.indexOf(",")));
				int y = Integer.decode(message.substring(message.indexOf(",")));
				GameAction.hex = new Point(x,y);
			}
		});
		
		GameAction.makeMove(this, team, GameAction.hex);
		GameAction.hex = null;
	}
	
	public void undoCalled(){
	}

	public void newgameCalled() {
	}
	
	public boolean supportsUndo() {
		new LANMessage("Want to play a new game?", LANGlobal.localPlayer.ip, LANGlobal.port);
		
		new LANReciever("", LANGlobal.localPlayer.ip, LANGlobal.port, new MessageRunnable(){
			@Override
			public void run() {
				// TODO Write code
			}});
		return false;
	}

	public boolean supportsNewgame() {
		return false;
	}
}