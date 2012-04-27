package com.sam.hex.replay;

import android.os.Handler;
import android.view.View;

import com.sam.hex.Global;
import com.sam.hex.HexGame;

public class Replay implements Runnable {
	int time;
	Handler handler;
	public Replay(int time, Handler handler){
		this.time = time;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		HexGame.stopGame();
		handler.post(new Runnable(){
			public void run() {
				Global.timerText.setVisibility(View.GONE);
				Global.winnerText.setVisibility(View.GONE);
//				Global.replayButtons.setVisibility(View.VISIBLE);
			}
		});
		Global.game.moveList.replay(time);
		Global.board.postInvalidate();
		HexGame.replayRunning = false;
		Global.game.start();
		handler.post(new Runnable(){
			public void run() {
				if(Global.game.totalTimerTime!=0) Global.timerText.setVisibility(View.VISIBLE);
//				Global.replayButtons.setVisibility(View.GONE);
			}
		});
	}
}
