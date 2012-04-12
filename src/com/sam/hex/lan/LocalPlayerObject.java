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
		if(Global.moveNumber!=1) new LANMessage("Move: "+Global.moveList.getmove().getX()+","+Global.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
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
			else if (Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
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
		LANGlobal.hex = new Point(-1,-1);
		listener.stop();
	}
	
	public boolean supportsUndo() {
		new LANMessage("Can I undo?", LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
		return false;
	}

	public boolean supportsNewgame() {
		new LANMessage("Want to play a new game?", LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
		return false;
	}

	@Override
	public void colorChanged() {
		if(team==1) new LANMessage("I changed my color to "+Global.player1Color, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		else new LANMessage("I changed my color to "+Global.player2Color, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
	}

	@Override
	public void nameChanged() {
		if(team==1) new LANMessage("I changed my name to "+Global.player1Name, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		else new LANMessage("I changed my name to "+Global.player2Name, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
	}

	@Override
	public void quit() {
		if(Global.moveNumber!=1) new LANMessage("Move: "+Global.moveList.getmove().getX()+","+Global.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
		listener.stop();
	}
}