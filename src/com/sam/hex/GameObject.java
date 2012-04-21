package com.sam.hex;

public class GameObject implements Runnable {
	private boolean game=true;
	private boolean threadAlive=true;

	public GameObject() {
		Global.gameThread = new Thread(this, "runningGame"); //Create a new thread.
		Global.gameOver = false;
		Global.gameThread.start(); //Start the thread.
	}
	
	public void start(){
		Global.gameOver = false;
		game=true;
	}
	
	public void stop(){
		game=false;
		threadAlive=false;
		Global.player1.quit();
		Global.player2.quit();
	}
	
	public void run() {
		while(threadAlive){//Keeps the thread alive even if the game has ended
			while(game){//Loop the game
				if(Global.currentPlayer == 1){
					if(GameAction.checkWinPlayer(2)){
						game=false;
						Global.player1.lose();
						Global.player2.win();
						announceWinner(2);
					}
					else{
						Global.player1.getPlayerTurn();
					}
				}
				else{
					if(GameAction.checkWinPlayer(1)){
						game=false;
						Global.player1.win();
						Global.player2.lose();
						announceWinner(1);
					}
					else{
						Global.player2.getPlayerTurn();
					}
				}
				Global.currentPlayer=(Global.currentPlayer%2)+1;
				GameAction.checkedFlagReset();
				Global.board.postInvalidate();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		new GameAction.AnnounceWinner(team);
	}
}