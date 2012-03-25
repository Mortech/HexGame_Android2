package com.sam.hex;

public class Replay implements Runnable {

	@Override
	public void run() {
		if(Global.game!=null){
    		Global.gameRunning=false;
    		Global.game.stop();
    		//Let the thread die
	    	try {
				Thread.sleep(110);
			}
	    	catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	Global.gameRunning=true;
    	}
		Global.moveList.replay(900);
		GameAction.checkedFlagReset();
		if(GameAction.checkWinPlayer1()){Global.currentPlayer=(Global.currentPlayer%2)+1; GameObject.announceWinner(1); return;}
		if(GameAction.checkWinPlayer2()){Global.currentPlayer=(Global.currentPlayer%2)+1; GameObject.announceWinner(2); return;}
		Global.board.postInvalidate();
		GameAction.hex=null;
		new GameObject();
	}
}
