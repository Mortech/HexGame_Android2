package com.sam.hex;

public class GameObject{
	public GameObject(){
		new Thread(new Runnable() {
			public void run() {
				while (Global.getRunning()) {
					if(Global.getCurrentPlayer()==(byte) 1){
		        		Global.getPlayer1().getPlayerTurn();
		        		//Check for victory
		        		if(BoardTools.checkWinPlayer1()) 
		        			Global.setRunning(false);
		        	}
		        	else{
		        		Global.getPlayer2().getPlayerTurn();
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
