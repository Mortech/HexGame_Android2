package com.sam.hex;

import android.os.Handler;
import android.view.View;

public class Timer implements Runnable{
	private boolean game = true;
	public long startTime;
	private long elapsedTime;
	private Handler handler;
	public int type;
	public long totalTime;
	private GameObject gameObject;
	
	public Timer(GameObject gameObject, long totalTime, int type){
		this.handler = gameObject.handler;
		this.gameObject = gameObject;
		this.totalTime = totalTime*60*1000;
		this.type = type;
		startTime = System.currentTimeMillis();
		gameObject.player1.setTime(this.totalTime);
		gameObject.player2.setTime(this.totalTime);
		
		start();
	}
	
	public void start(){
		game=true;
		if(type!=0){
			handler.post(new Runnable(){
				public void run(){
					gameObject.timerText.setVisibility(View.VISIBLE);
				}
			});
			new Thread(this).start();
		}
	}
	
	public void stop(){
		game=false;
	}
	
	public void run(){
		while(game){
			elapsedTime = System.currentTimeMillis()-startTime;
			if(gameObject.currentPlayer==1 && !gameObject.gameOver){
				gameObject.player1.setTime(totalTime-elapsedTime+totalTime-gameObject.player2.getTime());
				if(gameObject.player1.getTime()>0){
					handler.post(new Runnable(){
						public void run(){
							long millis = gameObject.player1.getTime();
					        int seconds = (int) (millis / 1000);
					        int minutes = seconds / 60;
					        seconds = seconds % 60;
					        gameObject.timerText.setText(GameAction.insert(gameObject.board.getContext().getString(R.string.timer),String.format("%d:%02d", minutes, seconds)));
					        gameObject.timerText.invalidate();
						}
					});
				}
				else{
					gameObject.player1.endMove();
				}
			}
			else if(gameObject.currentPlayer==2 && !gameObject.gameOver){
				gameObject.player2.setTime(totalTime-elapsedTime+totalTime-gameObject.player1.getTime());
				if(gameObject.player2.getTime()>0){
					handler.post(new Runnable(){
						public void run(){
							long millis = gameObject.player2.getTime();
					        int seconds = (int) (millis / 1000);
					        int minutes = seconds / 60;
					        seconds = seconds % 60;
					        gameObject.timerText.setText(GameAction.insert(gameObject.board.getContext().getString(R.string.timer),String.format("%d:%02d", minutes, seconds)));
					        gameObject.timerText.invalidate();
						}
					});
				}
				else{
					gameObject.player2.endMove();
				}
			}
			else{
				handler.post(new Runnable(){
					public void run(){
						gameObject.timerText.setVisibility(View.GONE);
					}
				});
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}