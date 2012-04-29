package com.sam.hex.replay;

import android.os.Handler;

import com.sam.hex.GameObject;
import com.sam.hex.HexGame;

public class Replay implements Runnable {
	private int time;
	private Handler handler;
	private GameObject game;
	private Runnable hideAnnouncementText;
	private Runnable showAnnouncementText;
	public Replay(int time, Handler handler, Runnable hideAnnouncementText, Runnable showAnnouncementText, GameObject game){
		this.time = time;
		this.handler = handler;
		this.hideAnnouncementText = hideAnnouncementText;
		this.showAnnouncementText = showAnnouncementText;
		this.game = game;
	}
	
	@Override
	public void run() {
		HexGame.stopGame(game);
		handler.post(hideAnnouncementText);
		game.moveList.replay(time, game);
		game.board.postInvalidate();
		HexGame.replayRunning = false;
		game.start();
		handler.post(showAnnouncementText);
	}
}
