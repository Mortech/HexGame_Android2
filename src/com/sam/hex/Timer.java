package com.sam.hex;

import android.os.Handler;
import android.view.View;

public class Timer implements Runnable{
	private boolean game = true;
	private long startTime;
	private long elapsedTime;
	private Handler handler;
	
	public Timer(Handler handler){
		this.handler = handler;
		startTime = System.currentTimeMillis();
		new Thread(this).start();
	}
	
	public void start(){
		game=true;
		if(Global.totalTimerTime!=0){
			handler.post(new Runnable(){
				public void run(){
					Global.timerText.setVisibility(View.VISIBLE);
				}
			});
		}
		new Thread(this).start();
	}
	
	public void stop(){
		game=false;
	}
	
	public void run(){
		while(game){
			elapsedTime = System.currentTimeMillis()-startTime;
			if(Global.currentPlayer==1 && !Global.gameOver){
				Global.player1Time = Global.totalTimerTime*60*1000-elapsedTime+Global.totalTimerTime*60*1000-Global.player2Time;
				if(Global.player1Time>0){
					handler.post(new Runnable(){
						public void run(){
							long millis = Global.player1Time;
					        int seconds = (int) (millis / 1000);
					        int minutes = seconds / 60;
					        seconds = seconds % 60;
							Global.timerText.setText(GameAction.InsertName.insert(Global.board.getContext().getString(R.string.timer),String.format("%d:%02d", minutes, seconds)));
							Global.timerText.invalidate();
						}
					});
				}
				else{
					Global.player1.endMove();
				}
			}
			else if(Global.currentPlayer==2 && !Global.gameOver){
				Global.player2Time = Global.totalTimerTime*60*1000-elapsedTime+Global.totalTimerTime*60*1000-Global.player1Time;
				if(Global.player2Time>0){
					handler.post(new Runnable(){
						public void run(){
							long millis = Global.player2Time;
					        int seconds = (int) (millis / 1000);
					        int minutes = seconds / 60;
					        seconds = seconds % 60;
							Global.timerText.setText(GameAction.InsertName.insert(Global.board.getContext().getString(R.string.timer),String.format("%d:%02d", minutes, seconds)));
							Global.timerText.invalidate();
						}
					});
				}
				else{
					Global.player2.endMove();
				}
			}
			else{
				handler.post(new Runnable(){
					public void run(){
						Global.timerText.setVisibility(View.GONE);
					}
				});
			}
			
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}