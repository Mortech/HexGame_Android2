package com.sam.hex.lan;

import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

import android.graphics.Point;

public class LocalPlayerObject implements PlayingEntity {
	byte[][] gameBoard; 
	byte team;
	UnicastListener listener;
	
	public LocalPlayerObject(byte team) {
		this.team=team;//Set the player's team
		listener = new UnicastListener();
	}

//	public Point getPlayerTurn(Point hex){
//		//TODO Create thread that listens for opponent's move
//		if(hex.equals(new Point(-1,-1))){
//			return new Point(-1,-1);
//		}
//		else if(Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
//			Global.gamePiece[hex.x][hex.y].setTeam((byte) ((team+1)%2));
//			Global.moveList.add(hex);
//			makeMove();
//			return Global.moveList.get(Global.moveList.size()-1);
//		}
//		else{
//			return new Point(-1,-1);
//		}
//	}
	
	//Do not use
	public void getPlayerTurn() {
		
	}
	
	public void undo(Point hex){
		Global.gamePiece[hex.x][hex.y].setTeam((byte)0);
	}
}