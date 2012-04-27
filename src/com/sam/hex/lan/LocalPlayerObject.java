package com.sam.hex.lan;

import android.graphics.Point;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

public class LocalPlayerObject implements PlayingEntity {
	private String name;
	private int color;
	private long timeLeft;
	private byte team;
	private PlayerUnicastListener listener;
	
	public LocalPlayerObject(byte team) {
		this.team=team;//Set the player's team
		listener = new PlayerUnicastListener(team);
	}
	
	public void getPlayerTurn() {
		if(Global.game.moveNumber>1){
			//Three times for reliability (I've really got to switch to tcp)
			new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
			new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
			new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		}
		
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
				if(Global.game.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				LANGlobal.hex = null;
				break;
			}
			else if (GameAction.makeMove(this, team, hex)) {
				LANGlobal.hex = null;
				break;
			}
			LANGlobal.hex = null;
		}
	}
	
	public void undoCalled(){
		LANGlobal.hex = new Point(-1,-1);
	}

	public void newgameCalled() {
		LANGlobal.hex = new Point(-1,-1);
	}
	
	public boolean supportsUndo() {
		//If they're Red, Blue played first, and that's the only move played so far, no, you cannot undo.
		if(!(Global.game.moveNumber==2 && LANGlobal.localPlayer.firstMove)) new LANMessage("Can I undo?", LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
		return false;
	}

	public boolean supportsNewgame() {
		new LANMessage("Want to play a new game?", LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		
		return false;
	}

	@Override
	public void quit() {
		new LANMessage("Quitting", LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		listener.stop();
	}
	
	@Override
	public void win() {
	}
	
	@Override
	public void lose() {
		new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("Move: "+Global.game.moveList.getmove().getX()+","+Global.game.moveList.getmove().getY(), LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
	}

	@Override
	public boolean supportsSave() {
		return false;
	}

	@Override
	public void endMove() {
		LANGlobal.hex = new Point(-1,-1);
	}

	@Override
	public void setName(String name) {
		this.name = name;
		new LANMessage("I changed my name to "+name, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("I changed my name to "+name, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("I changed my name to "+name, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		new LANMessage("I changed my color to "+color, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("I changed my color to "+color, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
		new LANMessage("I changed my color to "+color, LANGlobal.localPlayer.ip, LANGlobal.PLAYERPORT);
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setTime(long time) {
		this.timeLeft = time;
	}

	@Override
	public long getTime() {
		return timeLeft;
	}
}