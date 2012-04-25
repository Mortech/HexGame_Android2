package com.sam.hex;

import com.sam.hex.lan.LANGlobal;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class GameAction {
	public static Point hex;

	public static synchronized boolean checkWinPlayer(int team) {
		if(team==1){
			if(Global.totalTimerTime!=0 && Global.player2Time<0) return true;
			for (int i = 0; i < Global.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 1, Global.gridSize, i, Global.gamePiece)) {
					System.out.println("Player one wins");
					checkedFlagReset();
					String path=RegularPolygonGameObject.findShortestPath((byte) 1, Global.gridSize, i, Global.gamePiece);
					RegularPolygonGameObject.colorPath(Global.gridSize,i,path);
					return true;
				}
			}
			return false;
		}
		else{
			if(Global.totalTimerTime!=0 && Global.player1Time<0) return true;
			for (int i = 0; i < Global.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 2, i, Global.gridSize, Global.gamePiece)) {
					System.out.println("Player two wins");
					checkedFlagReset();
					String path=RegularPolygonGameObject.findShortestPath((byte) 2, i, Global.gridSize, Global.gamePiece);
					RegularPolygonGameObject.colorPath(i,Global.gridSize,path);
					return true;
				}
			}
			return false;
		}
	}

	public static void checkedFlagReset() {
		for (int x = Global.gridSize - 1; x >= 0; x--) {
			for (int y = Global.gridSize - 1; y >= 0; y--) {
				Global.gamePiece[x][y].checkedflage = false;
			}
		}
	}
	
	public static void setPiece(Point p) {
		hex = p;
	}
	
	private static void setTeam(byte t,int x,int y) {
		Global.moveList.makeMove(x, y, t, System.currentTimeMillis()-Global.game.moveStart);
		Global.gamePiece[x][y].setTeam(t);
		Global.moveNumber++;
		Global.board.postInvalidate();
	}
	
	public static boolean makeMove(PlayingEntity player, byte team, Point hex){
		if(player!=null && Global.gamePiece[hex.x][hex.y].getTeam() == 0){
			setTeam(team,hex.x,hex.y);
			return true;
		}
		else if(player!=null && Global.moveNumber==2 && Global.gamePiece[hex.x][hex.y].getTeam() == 1){//Swap rule
	    	if(Global.swap){
				setTeam(team,hex.x,hex.y);
				return true;
	    	}
		}
		return false;
	}
	
	public static void undo(){
		if(Global.moveNumber>1){
			GameAction.checkedFlagReset();
			
			//Remove the piece from the board and the movelist
			Move lastMove = Global.moveList.thisMove;
			Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
			Global.moveList = Global.moveList.nextMove;
			Global.moveList.replay(0);
			Global.moveNumber--;
			
			if(Global.gameLocation==0){
				//Determine who is a human
				boolean p1 = Global.player1 instanceof PlayerObject;
				boolean p2 = Global.player2 instanceof PlayerObject;
				if(Global.gameOver) Global.currentPlayer = (Global.currentPlayer%2)+1;
				
				if(Global.currentPlayer==1 && p1){//It's a human's turn
					Global.player2.undoCalled();//Tell the other player we're going back a turn
					
					if(!p2){//If the other person isn't a human, undo again
						if(Global.moveNumber>1){
							lastMove = Global.moveList.thisMove;
							Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
							Global.moveList = Global.moveList.nextMove;
							Global.moveNumber--;
						}
						else{
							GameAction.hex = new Point(-1,-1);
						}
					}
					else{
						//Otherwise, cede the turn to the other player
						GameAction.hex = new Point(-1,-1);
					}
				}
				else if(Global.currentPlayer==1 && !p1){
					if(!Global.gameOver){
						Global.player1.undoCalled();
					}
				}
				else if(Global.currentPlayer==2 && p2){
					Global.player1.undoCalled();
					
					//If the other person isn't a (local) human
					if(!p1){
						//Undo again
						if(Global.moveNumber>1){
							lastMove = Global.moveList.thisMove;
							Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
							Global.moveList = Global.moveList.nextMove;
							Global.moveNumber--;
						}
						else{
							GameAction.hex = new Point(-1,-1);
						}
					}
					else{
						//Otherwise, cede the turn to the other player
						GameAction.hex = new Point(-1,-1);
					}
				}
				else if(Global.currentPlayer==2 && !p2){
					if(!Global.gameOver) {
						Global.player2.undoCalled();
					}
				}
				if(Global.gameOver && ((Global.currentPlayer==2 && p1) || (Global.currentPlayer==1 && p2))) Global.currentPlayer = (Global.currentPlayer%2)+1;
			}
			if(Global.gameLocation==1){//Inside a LAN game
				if(Global.currentPlayer==1){//First player's turn
					if(LANGlobal.localPlayer.firstMove){//First player is on the network (not local)
						if(LANGlobal.undoRequested){//First player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>1){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
								Global.moveNumber--;
							}
							if(Global.gameOver) Global.currentPlayer = (Global.currentPlayer%2)+1;
						}
						else{//Second player requested the undo
							//undo once, switch players
							LANGlobal.hex = new Point(-1,-1);
						}
					}
					else{//First player is local (not on the network)
						if(LANGlobal.undoRequested){//Second player requested the undo
							//undo once, switch players
							GameAction.hex = new Point(-1,-1);
						}
						else{//First player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>1){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
								Global.moveNumber--;
							}
							if(Global.gameOver) Global.currentPlayer = (Global.currentPlayer%2)+1;
						}
					}
				}
				else{//Second player's turn
					if(LANGlobal.localPlayer.firstMove){//Second player is local (not on the network)
						if(LANGlobal.undoRequested){//First player requested the undo
							//undo once, switch players
							GameAction.hex = new Point(-1,-1);
						}
						else{//Second player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>1){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
								Global.moveNumber--;
							}
							if(Global.gameOver) Global.currentPlayer = (Global.currentPlayer%2)+1;
						}
					}
					else{//Second player is on the network (not local)
						if(LANGlobal.undoRequested){//Second player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>1){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
								Global.moveNumber--;
							}
							if(Global.gameOver) Global.currentPlayer = (Global.currentPlayer%2)+1;
						}
						else{//First player requested the undo
							//undo once, switch players
							LANGlobal.hex = new Point(-1,-1);
						}
					}
				}
				
				LANGlobal.undoRequested = false;
			}
			
			//Reset the game if it's already ended
			if(Global.gameOver){
				Global.moveList.replay(0);
				Global.game.start();
			}
		}
		
		Global.board.postInvalidate();
	}
	
	public static class AnnounceWinner implements Runnable{
		private static Handler handler;
		int team;
		public AnnounceWinner(int team){
			this.team = team;
			
			new Thread(this).start();
		}
		
		@Override
		public void run(){
			if(handler!=null) handler.getLooper().quit();
			Looper.prepare();
			handler = new Handler();
			if(team==1){
				Toast.makeText(Global.board.getContext(), InsertName.insert(Global.board.getContext().getString(R.string.winner), Global.player1Name), Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(Global.board.getContext(), InsertName.insert(Global.board.getContext().getString(R.string.winner), Global.player2Name), Toast.LENGTH_SHORT).show();
			}
			Looper.loop();
		}
	}
	
	public static class InsertName{
		public static String insert(String text, String name){
			String inserted = text.replaceAll("#",name);
			return inserted;
		}
	}
	
	public static PlayingEntity getPlayer(int i){
		if(i==1){
			return Global.player1;
		}
		else if(i==2){
			return Global.player2;
		}
		else{
			return null;
		}
	}

	final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String pointToString(Point p){
		String str = "";
		str += alphabet.charAt(p.y);
		str += (p.x+1);
		return str;
	}
	
	public static Point stringToPoint(String str){
		int x = Integer.parseInt(str.substring(1))-1;
		char y = str.charAt(0);
		
		return new Point(x, alphabet.indexOf(y));
	}
}