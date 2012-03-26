package com.sam.hex;

import android.os.Looper;
import android.widget.Toast;

public class GameObject implements Runnable {
	public boolean go=true;

	public GameObject() {
		Global.gameThread = new Thread(this, "runningGame"); //Create a new thread.
		Global.gameOver = false;
		Global.gameThread.start(); //Start the thread.
	}
	
	public void stop(){
		go=false;
	}
	
	public void run() {
		//Loop the game
		while(go){
			if (Global.currentPlayer == 1) {
				Global.player1.getPlayerTurn();
				if (GameAction.checkWinPlayer1()){
					go=false;
					announceWinner(1);
				}
				
				Global.currentPlayer=(Global.currentPlayer%2)+1;
			}
			else {
				Global.player2.getPlayerTurn();
				if (GameAction.checkWinPlayer2()){
					go=false;
					announceWinner(2);
				}
				
				Global.currentPlayer=(Global.currentPlayer%2)+1;
			}
			GameAction.checkedFlagReset();
			Global.moveNumber++;
			Global.board.postInvalidate();
		}
		
		System.out.println("Thread died");
	}
	
	public static void announceWinner(int team){
		Global.gameOver = true;
		Global.board.postInvalidate();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Looper.prepare();
		if(team==1){
			Toast.makeText(Global.board.getContext(), Global.player1Name+" wins!", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(Global.board.getContext(), Global.player2Name+" wins!", Toast.LENGTH_SHORT).show();
		}
		Looper.loop();
	}
}