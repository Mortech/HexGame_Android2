package com.sam.hex;

import android.view.View;

public class GameObject implements Runnable {
	private boolean game=true;
	public int moveNumber;
	public MoveList moveList;
	public int currentPlayer;
	public boolean gameOver=false;
	public PlayingEntity player1;
	public PlayingEntity player2;
	public long moveStart;
	public Thread gameThread;
	public int gridSize;
	public boolean swap;
	public Timer timer;
	public RegularPolygonGameObject[][] gamePiece;

	public GameObject() {
		gameThread = new Thread(this, "runningGame"); //Create a new thread.
		
		moveNumber=1;
		moveList= new MoveList();
		currentPlayer = 1;
		game=true;
		gameOver=false;
	}
	
	public void start(){
		if(gameOver) GameAction.handler.post(new Runnable(){
			public void run(){
				Global.winnerText.setVisibility(View.GONE);
			}
		});
		gameOver=false;
		game=true;
		timer.start();
		gameThread = new Thread(this, "runningGame");
		gameThread.start();
	}
	
	public void stop(){
		game=false;
		gameOver=true;
		timer.stop();
		player1.quit();
		player2.quit();
	}
	
	public void run() {
		while(game){//Loop the game
			if(!checkForWinner()){
				moveStart = System.currentTimeMillis();
				GameAction.getPlayer(currentPlayer).getPlayerTurn();
				if(timer.type==1){
					timer.startTime = System.currentTimeMillis();
					GameAction.getPlayer(currentPlayer).setTime(timer.totalTime);
				}
			}
			
			currentPlayer=(currentPlayer%2)+1;
		}
		System.out.println("Thread died");
	}
	
	private void announceWinner(int team){
		Global.board.postInvalidate();
		new GameAction.AnnounceWinner(team);
	}
	
	private boolean checkForWinner(){
		GameAction.checkedFlagReset();
		if(GameAction.checkWinPlayer(1)){
			game=false;
			gameOver = true;
			player1.win();
			player2.lose();
			announceWinner(1);
		}
		else if(GameAction.checkWinPlayer(2)){
			game=false;
			gameOver = true;
			player1.lose();
			player2.win();
			announceWinner(2);
		}
		
		return gameOver;
	}
}