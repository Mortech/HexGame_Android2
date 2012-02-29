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

	
	public void getPlayerTurn() {
		this.gameBoard=BoardTools.teamGrid();
		makeMove();
	}
	public void makeMove(){
		while(Global.getPendingMove()==null){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BoardTools.makeMove(Global.getPendingMove().getX(), Global.getPendingMove().getY(), Global.getCurrentPlayer());
		Global.setPendingMove(null);
	}

}
