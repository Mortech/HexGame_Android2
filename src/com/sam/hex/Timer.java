package com.sam.hex;

import android.view.View;

public class Timer implements Runnable{
	private boolean refresh = true;
	public long startTime;
	private long elapsedTime;
	public int type;
	public long totalTime;
	private GameObject game;
	private int currentPlayer;
	
	public Timer(GameObject game, long totalTime, int type){
		this.game = game;
		this.totalTime = totalTime*60*1000;
		this.type = type;
		startTime = System.currentTimeMillis();
		game.player1.setTime(this.totalTime);
		game.player2.setTime(this.totalTime);
	}
	
	public void start(){
		refresh=true;
		if(type!=0){
			game.handler.post(new Runnable(){
				public void run(){
					game.timerText.setVisibility(View.VISIBLE);
				}
			});
			new Thread(this).start();
		}
	}
	
	public void stop(){
		refresh=false;
	}
	
	public void run(){
		while(refresh){
			elapsedTime = System.currentTimeMillis()-startTime;
			currentPlayer = game.currentPlayer;
			
			if(!game.gameOver){
				GameAction.getPlayer(currentPlayer, game).setTime(calculatePlayerTime(currentPlayer));
				if(GameAction.getPlayer(currentPlayer, game).getTime()>0){
					displayTime();
				}
				else{
					GameAction.getPlayer(currentPlayer, game).endMove();
					game.board.postInvalidate();
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private long calculatePlayerTime(int player){
		return totalTime-elapsedTime+totalTime-GameAction.getPlayer(player%2+1, game).getTime();
	}
	
	private void displayTime(){
		game.handler.post(new Runnable(){
			public void run(){
				long millis = GameAction.getPlayer(game.currentPlayer, game).getTime();
		        int seconds = (int) (millis / 1000);
		        int minutes = seconds / 60;
		        seconds = seconds % 60;
		        game.timerText.setText(GameAction.insert(game.board.getContext().getString(R.string.timer),String.format("%d:%02d", minutes, seconds)));
		        game.timerText.invalidate();
			}
		});
	}
}