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
		Global.board.postInvalidate();
		GameAction.hex=null;
		HexGame.replayRunning = false;
		Global.game = new GameObject();
	}
}
