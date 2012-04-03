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
		listener = new UnicastListener(team);
	}
	
	public void getPlayerTurn() {
		new LANMessage("Move: "+Global.moveList.getmove().getX()+","+Global.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.playerPort);
		
		new LANReciever("Move: ", LANGlobal.localPlayer.ip, LANGlobal.playerPort, new MessageRunnable(){
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
		new LANReciever("undo", LANGlobal.localPlayer.ip, LANGlobal.undoPort, new MessageRunnable(){
			@Override
			public void run() {
				if(message.contains("Sure")){
					GameAction.newGame = true;
				}
				else if(message.contains("No")){
					GameAction.newGame = false;
				}
			}});
		return GameAction.newGame;
	}

	public boolean supportsNewgame() {
		new LANReciever("play again", LANGlobal.localPlayer.ip, LANGlobal.newgamePort, new MessageRunnable(){
			@Override
			public void run() {
				if(message.contains("Sure")){
					GameAction.newGame = true;
				}
				else if(message.contains("No")){
					GameAction.newGame = false;
				}
			}});
		return GameAction.newGame;
	}
}