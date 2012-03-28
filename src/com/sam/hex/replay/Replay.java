package com.sam.hex.replay;

import com.sam.hex.GameAction;
import com.sam.hex.GameObject;
import com.sam.hex.Global;
import com.sam.hex.HexGame;

public class Replay implements Runnable {

	@Override
	public void run() {
		if(Global.game!=null && Global.game.go){
    		Global.gameOver=true;
    		Global.game.stop();
    		//Let the thread die
	    	try {
				Global.gameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	Global.gameOver=false;
    	}
		Global.moveList.replay(900);
		GameAction.checkedFlagReset();
		if(HexGame.replayRunning)if(GameAction.checkWinPlayer1()){Global.currentPlayer=(Global.currentPlayer%2)+1; GameObject.announceWinner(1); return;}
		if(HexGame.replayRunning)if(GameAction.checkWinPlayer2()){Global.currentPlayer=(Global.currentPlayer%2)+1; GameObject.announceWinner(2); return;}
		Global.board.postInvalidate();
		GameAction.hex=null;
		new GameObject();
	}
}
