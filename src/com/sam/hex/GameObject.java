package com.sam.hex;

public class GameObject{
	public GameObject(){
		new Thread(new Runnable() {
			public void run() {
				while (Global.getRunning()) {
					if (Global.getCurrentPlayer() == 1) {
						Global.getPlayer1().getPlayerTurn();
						
						//Check for victory
			        	if(Global.getCurrentPlayer()==(byte) 1){
			        		if(BoardTools.checkWinPlayer1()) 
			        			Global.setRunning(false);
			        	}
			        	else{
			        		if(BoardTools.checkWinPlayer2()) 
			        			Global.setRunning(false);
			        	}
			        	
			        	//Update the player, refresh the board
			        	Global.getBoard().postInvalidate();
						BoardTools.updateCurrentPlayer();
					} 
					else {
						Global.getPlayer2().getPlayerTurn();
						
						//Check for victory
			        	if(Global.getCurrentPlayer()==(byte) 1){
			        		if(BoardTools.checkWinPlayer1()) 
			        			Global.setRunning(false);
			        	}
			        	else{
			        		if(BoardTools.checkWinPlayer2()) 
			        			Global.setRunning(false);
			        	}
			        	
			        	//Update the player, refresh the board
			        	Global.getBoard().postInvalidate();
						BoardTools.updateCurrentPlayer();
					}
				}
			}
		}).start();
	}
}
