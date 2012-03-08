package com.sam.hex;

public class PlayerObject implements PlayingEntity {
	
	byte[][] gameBoard; 
	byte team;
	
	public PlayerObject(byte i) {
	this.team=i;	//sets the players team
	}

	
	public void getPlayerTurn(byte[][] gameBoard) {
		 this.gameBoard=gameBoard;
		 makeMove();
	}

	public boolean getPlayerTurn(RegularPolygonGameObject hex){
		if (hex!=null && hex.getTeam() == 0) {
			hex.setTeam(team);
			return true;
		}
		return false;
	}
	public void getPlayerTurn() {
		this.gameBoard=BoardTools.teamGrid();
		makeMove();
	}
	public void makeMove(){
		GameAction.getPlayerTurn(team); // lets the player make his move
	}

}
