package com.sam.hex;

import android.graphics.Point;

public class LocalPlayerObject implements PlayingEntity {
	
	byte[][] gameBoard; 
	byte team;
	
	public LocalPlayerObject(byte i) {
		this.team=i;//Set the player's team
	}

	
	public void getPlayerTurn(byte[][] gameBoard) {
		 this.gameBoard=gameBoard;
		 makeMove();
	}

	public boolean getPlayerTurn(Point hex){
		if (hex!=null && Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
			Global.gamePiece[hex.x][hex.y].setTeam(team);
			Global.moveList.add(hex);
			return true;
		}
		return false;
	}
	
	public void getPlayerTurn() {
		this.gameBoard=BoardTools.teamGrid();
		makeMove();
	}
	
	public void makeMove(){
		GameAction.getPlayerTurn(team);//Have the player make a move
	}
}