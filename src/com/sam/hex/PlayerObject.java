package com.sam.hex;

import android.graphics.Point;

public class PlayerObject implements PlayingEntity {
	
	byte[][] gameBoard; 
	byte team;
	
	public PlayerObject(byte i) {
	this.team=i;//Set the player's team
	}
	
	public void getPlayerTurn() {
		looper: while (true) {
			Point hex = GameAction.hex;
			while (hex == null) {
				hex = GameAction.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!Global.gameRunning) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				GameAction.hex = null;
				break;
			}
			if (Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
				GameAction.makeMove(this, team, hex);
				GameAction.hex = null;
				break;
			}
			GameAction.hex = null;
		}
	}
	
	public void undo(Point hex){
	}
}