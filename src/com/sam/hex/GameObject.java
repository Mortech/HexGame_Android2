package com.sam.hex;

import android.os.Handler;

public class GameObject implements Runnable {
	private boolean game=true;
	private boolean threadAlive=true;
	public long moveStart;

	public GameObject() {
		Global.gameThread = new Thread(this, "runningGame"); //Create a new thread.
		Global.gameOver = false;
		Global.gameThread.start(); //Start the thread.
	}
	
	public void start(){
		Global.gameOver = false;
		game=true;
		if(Global.totalTimerTime!=0) Global.timer.start();
	}
	
	public void stop(){
		game=false;
		threadAlive=false;
		if(Global.totalTimerTime!=0) Global.timer.stop();
		Global.player1.quit();
		Global.player2.quit();
	}
	
	public void run() {
    	if(Global.totalTimerTime!=0) Global.timer = new Timer(new Handler());
		while(threadAlive){//Keeps the thread alive even if the game has ended
			while(game){//Loop the game
				if(!checkForWinner()){
					moveStart = System.currentTimeMillis();
					if(Global.currentPlayer == 1){
						
						Global.player1.getPlayerTurn();
					}
					else{
						Global.player2.getPlayerTurn();
					}
				}
				
				Global.currentPlayer=(Global.currentPlayer%2)+1;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Thread died");
	}
	
	private static void announceWinner(int team){
		Global.gameOver = true;
		Global.board.postInvalidate();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new GameAction.AnnounceWinner(team);
	}
	
	private boolean checkForWinner(){
		boolean victory = false;
		GameAction.checkedFlagReset();
		if(GameAction.checkWinPlayer(1)){
			victory = true;
			game=false;
			Global.player1.win();
			Global.player2.lose();
			announceWinner(1);
		}
		else if(GameAction.checkWinPlayer(2)){
			victory = true;
			game=false;
			Global.player1.lose();
			Global.player2.win();
			announceWinner(2);
		}
		
		return victory;
	}
}