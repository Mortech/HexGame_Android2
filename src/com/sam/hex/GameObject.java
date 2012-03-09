package com.sam.hex;

import android.graphics.Point;

public class GameObject implements Runnable {
	Thread theGameRunner;
	private Point hex;
	PlayingEntity player1;
	PlayingEntity player2;
	byte player = 1;
	boolean go=true;

	public GameObject() {
		theGameRunner = new Thread(this, "runningGame"); // (1) Create a new
		// thread.
		System.out.println(theGameRunner.getName());
		if(Global.gameType<2) player1=new PlayerObject((byte)1);
		else player1=new GameAI((byte)1,(byte)1);// sets player vs Ai
		
		if((Global.gameType+1)%2>0) player2=new PlayerObject((byte)2);
		else player2=new GameAI((byte)2,(byte)1);// sets player vs Ai
		replay();
		
		theGameRunner.start(); // (2) Start the thread.
		
	}
	public void replay(){
		Global.slowAI=false;
		for(int i=0; i<Global.moveList.size(); i++){
			if(Global.moveList.get(i)==null){
				if(player==1)
					player1.getPlayerTurn();
				else player2.getPlayerTurn();
			}
			else{	
				Global.gamePiece[Global.moveList.get(i).x][Global.moveList.get(i).y].setTeam(player);
			}
			
			if(player==1)
				player=2;
			else
				player=1;
		}
		if(go && (GameAction.checkWinPlayer1() || GameAction.checkWinPlayer2())){
			go=false;
		}
			GameAction.checkedFlagReset();
		hex=null;
		Global.slowAI=true;
		Global.board.postInvalidate();
	}
	public void stop(){
		//theGameRunner.stop();
		go=false;
	}
	public void setPiece(Point h){
		hex=h;
	}
	public void run() {
		while(go){
			
			if(go) doStuff();
			Global.board.postInvalidate();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//if(go &&(GameAction.checkWinPlayer1() || GameAction.checkWinPlayer2()))
				//go=false;
		}
		
	}
	public void doStuff(){
		if (player == 1) {
			boolean success=true;
			if(player1 instanceof PlayerObject)
				success=((PlayerObject)player1).getPlayerTurn(hex);
			else
				player1.getPlayerTurn();
			hex=null;
			if (success && GameAction.checkWinPlayer1())
				go=false;
			
			if(success)
				player = 2;
			GameAction.checkedFlagReset();
		} else {
			boolean success=true;
			if(player2 instanceof PlayerObject)
				success=((PlayerObject)player2).getPlayerTurn(hex);
			else
				player2.getPlayerTurn();
			hex=null;
			if (success && GameAction.checkWinPlayer2())
				go=false;
			if(success)
				player = 1;
			GameAction.checkedFlagReset();
		}
		Global.board.postInvalidate();
	}
}
