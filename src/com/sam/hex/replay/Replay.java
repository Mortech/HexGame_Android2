package com.sam.hex.replay;

import com.sam.hex.GameAction;
import com.sam.hex.GameObject;
import com.sam.hex.Global;
import com.sam.hex.HexGame;

public class Replay implements Runnable {
	int time;
	public Replay(int time){
		this.time = time;
	}
	
	@Override
	public void run() {
		HexGame.stopGame();
		Global.moveList.replay(time);
		GameAction.checkedFlagReset();
		if(HexGame.replayRunning)if(GameAction.checkWinPlayer(1)){GameObject.announceWinner(1); return;}
		if(HexGame.replayRunning)if(GameAction.checkWinPlayer(2)){GameObject.announceWinner(2); return;}
		Global.board.postInvalidate();
		GameAction.hex=null;
		HexGame.replayRunning = false;
		Global.game = new GameObject();
	}
}
