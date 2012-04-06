package com.sam.hex.lan;

import android.graphics.Point;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

public class LocalPlayerObject implements PlayingEntity {
	byte[][] gameBoard; 
	byte team;
	PlayerUnicastListener listener;
	
	public LocalPlayerObject(byte team) {
		this.team=team;//Set the player's team
		listener = new PlayerUnicastListener(team);
	}
	
	public void getPlayerTurn() {
		System.out.println("Yay, my turn");
		
		if(Global.moveNumber!=1) new LANMessage("Move: "+Global.moveList.getmove().getX()+","+Global.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.playerPort);
		
		LANGlobal.hex = null;
		looper: while (true) {
			Point hex = LANGlobal.hex;
			while (hex == null) {
				hex = LANGlobal.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Global.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				LANGlobal.hex = null;
				break;
			}
			if (Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
				GameAction.makeMove(this, team, hex);
				LANGlobal.hex = null;
				break;
			}
			LANGlobal.hex = null;
		}
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
		LANGlobal.hex = new Point(-1,-1);
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