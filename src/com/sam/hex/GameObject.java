package com.sam.hex;

public class GameObject{
	public GameObject(){
		new Thread(new Runnable() {
			public void run() {
				while (Global.getRunning()) {
					//Patch - Game doesn't end correctly (sometimes). This is a failsafe.
		        	for(int i=0;i<Global.getN();i++){
						for(int j=0;j<Global.getN();j++){
							if(Global.getGameboard()[i][j]==3 || Global.getGameboard()[i][j]==4){
								return;
							}
						}
					}
					if(Global.getCurrentPlayer()==(byte) 1){
		        		Global.getPlayer1().getPlayerTurn();
		        		//Check for victory
		        		if(BoardTools.checkWinPlayer1()) 
		        			Global.setRunning(false);
		        	}
		        	else if(Global.getCurrentPlayer()==(byte) 2){
		        		Global.getPlayer2().getPlayerTurn();
		        		//Check for victory
		        		if(BoardTools.checkWinPlayer2()) 
		        			Global.setRunning(false);
		        	}
		        	
		        	//Update the player, refresh the board
		        	BoardTools.updateCurrentPlayer();
		        	Global.getBoard().postInvalidate();
				}
			}
		}).start();
	}
}
