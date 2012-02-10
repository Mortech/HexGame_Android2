package com.sam.hex;

//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;

public class GameObject implements Runnable {
	Thread theGameRunner;

	public GameObject() {
		theGameRunner = new Thread(this, "runningGame"); // (1) Create a new
		// thread.
		System.out.println(theGameRunner.getName());
		theGameRunner.start(); // (2) Start the thread.
	}

	public void run() {
		PlayingEntity player1;
		PlayingEntity player2;
		
		if(Global.getGameType()<2) player1=new PlayerObject((byte)1);
		else player1=new GameAI((byte)1,(byte)1);// sets player vs Ai
		
		if((Global.getGameType()+1)%2>0) player2=new PlayerObject((byte)2);
		else player2=new GameAI((byte)2,(byte)1);// sets player vs Ai
		
		
		while (true) {

			if (Global.getCurrentPlayer() == 1) {
				player1.getPlayerTurn();
				if (GameAction.checkWinPlayer1())
					break;
				BoardTools.updateCurrentPlayer();
			} else {
				player2.getPlayerTurn();
				if (GameAction.checkWinPlayer2())
					break;
				BoardTools.updateCurrentPlayer();
			}

		}

	}

}
