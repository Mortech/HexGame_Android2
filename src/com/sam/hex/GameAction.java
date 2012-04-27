package com.sam.hex;

import com.sam.hex.lan.LANGlobal;

import android.graphics.Point;
import android.os.Handler;
import android.view.View;

public class GameAction {
	public static Handler handler;

	public static synchronized boolean checkWinPlayer(int team) {
		if(team==1){
			if(Global.game.timer.type!=0 && Global.game.player2.getTime()<0) return true;
			for (int i = 0; i < Global.game.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 1, Global.game.gridSize, i, Global.game.gamePiece)) {
					System.out.println("Player one wins");
					checkedFlagReset();
					String path=RegularPolygonGameObject.findShortestPath((byte) 1, Global.game.gridSize, i, Global.game.gamePiece);
					RegularPolygonGameObject.colorPath(Global.game.gridSize,i,path);
					return true;
				}
			}
			return false;
		}
		else{
			if(Global.game.timer.type!=0 && Global.game.player1.getTime()<0) return true;
			for (int i = 0; i < Global.game.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 2, i, Global.game.gridSize, Global.game.gamePiece)) {
					System.out.println("Player two wins");
					checkedFlagReset();
					String path=RegularPolygonGameObject.findShortestPath((byte) 2, i, Global.game.gridSize, Global.game.gamePiece);
					RegularPolygonGameObject.colorPath(i,Global.game.gridSize,path);
					return true;
				}
			}
			return false;
		}
	}

	public static void checkedFlagReset() {
		for (int x = Global.game.gridSize - 1; x >= 0; x--) {
			for (int y = Global.game.gridSize - 1; y >= 0; y--) {
				Global.game.gamePiece[x][y].checkedflage = false;
			}
		}
	}
	
	public static void setPiece(Point p) {
		getPlayer(Global.game.currentPlayer).setMove(p);
	}
	
	private static void setTeam(byte t,int x,int y) {
		Global.game.moveList.makeMove(x, y, t, System.currentTimeMillis()-Global.game.moveStart);
		Global.game.gamePiece[x][y].setTeam(t);
		Global.game.moveNumber++;
		Global.board.postInvalidate();
	}
	
	public static boolean makeMove(PlayingEntity player, byte team, Point hex){
		if(player!=null && Global.game.gamePiece[hex.x][hex.y].getTeam() == 0){
			setTeam(team,hex.x,hex.y);
			return true;
		}
		else if(player!=null && Global.game.moveNumber==2 && Global.game.gamePiece[hex.x][hex.y].getTeam() == 1){//Swap rule
	    	if(Global.game.swap){
				setTeam(team,hex.x,hex.y);
				return true;
	    	}
		}
		return false;
	}
	
	public static void undo(){
		if(Global.game.moveNumber>1){
			GameAction.checkedFlagReset();
			
			//Remove the piece from the board and the movelist
			Move lastMove = Global.game.moveList.thisMove;
			Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
			Global.game.moveList = Global.game.moveList.nextMove;
			Global.game.moveList.replay(0);
			Global.game.moveNumber--;
			
			if(Global.gameLocation==0){
				//Determine who is a human
				boolean p1 = Global.game.player1 instanceof PlayerObject;
				boolean p2 = Global.game.player2 instanceof PlayerObject;
				if(Global.game.gameOver) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
				
				if(Global.game.currentPlayer==1 && p1){//It's a human's turn
					Global.game.player2.undoCalled();//Tell the other player we're going back a turn
					
					if(!p2){//If the other person isn't a human, undo again
						if(Global.game.moveNumber>1){
							lastMove = Global.game.moveList.thisMove;
							Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
							Global.game.moveList = Global.game.moveList.nextMove;
							Global.game.moveNumber--;
						}
						else{
							getPlayer(Global.game.currentPlayer).endMove();
						}
					}
					else{
						//Otherwise, cede the turn to the other player
						getPlayer(Global.game.currentPlayer).endMove();
					}
				}
				else if(Global.game.currentPlayer==1 && !p1){
					if(!Global.game.gameOver){
						Global.game.player1.undoCalled();
					}
				}
				else if(Global.game.currentPlayer==2 && p2){
					Global.game.player1.undoCalled();
					
					//If the other person isn't a (local) human
					if(!p1){
						//Undo again
						if(Global.game.moveNumber>1){
							lastMove = Global.game.moveList.thisMove;
							Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
							Global.game.moveList = Global.game.moveList.nextMove;
							Global.game.moveNumber--;
						}
						else{
							getPlayer(Global.game.currentPlayer).endMove();
						}
					}
					else{
						//Otherwise, cede the turn to the other player
						getPlayer(Global.game.currentPlayer).endMove();
					}
				}
				else if(Global.game.currentPlayer==2 && !p2){
					if(!Global.game.gameOver) {
						Global.game.player2.undoCalled();
					}
				}
				if(Global.game.gameOver && ((Global.game.currentPlayer==2 && p1) || (Global.game.currentPlayer==1 && p2))) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
			}
			if(Global.gameLocation==1){//Inside a LAN game
				if(Global.game.currentPlayer==1){//First player's turn
					if(LANGlobal.localPlayer.firstMove){//First player is on the network (not local)
						if(LANGlobal.undoRequested){//First player requested the undo
							//undo twice, don't switch players
							if(Global.game.moveNumber>1){
								lastMove = Global.game.moveList.thisMove;
								Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.game.moveList = Global.game.moveList.nextMove;
								Global.game.moveNumber--;
							}
							if(Global.game.gameOver) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
						}
						else{//Second player requested the undo
							//undo once, switch players
							GameAction.getPlayer(Global.game.currentPlayer).endMove();
						}
					}
					else{//First player is local (not on the network)
						if(LANGlobal.undoRequested){//Second player requested the undo
							//undo once, switch players
							getPlayer(Global.game.currentPlayer).endMove();
						}
						else{//First player requested the undo
							//undo twice, don't switch players
							if(Global.game.moveNumber>1){
								lastMove = Global.game.moveList.thisMove;
								Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.game.moveList = Global.game.moveList.nextMove;
								Global.game.moveNumber--;
							}
							if(Global.game.gameOver) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
						}
					}
				}
				else{//Second player's turn
					if(LANGlobal.localPlayer.firstMove){//Second player is local (not on the network)
						if(LANGlobal.undoRequested){//First player requested the undo
							//undo once, switch players
							getPlayer(Global.game.currentPlayer).endMove();
						}
						else{//Second player requested the undo
							//undo twice, don't switch players
							if(Global.game.moveNumber>1){
								lastMove = Global.game.moveList.thisMove;
								Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.game.moveList = Global.game.moveList.nextMove;
								Global.game.moveNumber--;
							}
							if(Global.game.gameOver) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
						}
					}
					else{//Second player is on the network (not local)
						if(LANGlobal.undoRequested){//Second player requested the undo
							//undo twice, don't switch players
							if(Global.game.moveNumber>1){
								lastMove = Global.game.moveList.thisMove;
								Global.game.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.game.moveList = Global.game.moveList.nextMove;
								Global.game.moveNumber--;
							}
							if(Global.game.gameOver) Global.game.currentPlayer = (Global.game.currentPlayer%2)+1;
						}
						else{//First player requested the undo
							//undo once, switch players
							GameAction.getPlayer(Global.game.currentPlayer).endMove();
						}
					}
				}
				
				LANGlobal.undoRequested = false;
			}
			
			//Reset the game if it's already ended
			if(Global.game.gameOver){
				Global.game.moveList.replay(0);
				Global.game.start();
			}
		}
		
		Global.board.postInvalidate();
	}
	
	public static class AnnounceWinner{
		public AnnounceWinner(final int team){
			GameAction.handler.post(new Runnable(){
				public void run(){
					Global.winnerMsg = insert(Global.board.getContext().getString(R.string.winner), getPlayer(team).getName());
					Global.winnerText.setText(Global.winnerMsg);
					Global.winnerText.setVisibility(View.VISIBLE);
					Global.winnerText.invalidate();
				}
			});
		}
	}
	
	public static String insert(String text, String name){
		String inserted = text.replaceAll("#",name);
		return inserted;
	}
	
	public static PlayingEntity getPlayer(int i){
		if(i==1){
			return Global.game.player1;
		}
		else if(i==2){
			return Global.game.player2;
		}
		else{
			return null;
		}
	}

	final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String pointToString(Point p){
		if(Global.game.moveNumber==2 && Global.game.moveList.thisMove.equals(Global.game.moveList.nextMove.thisMove)) return "SWAP";
		String str = "";
		str += alphabet.charAt(p.y);
		str += (p.x+1);
		return str;
	}
	
	public static Point stringToPoint(String str){
		if(str.equals("SWAP")) return new Point(Global.game.moveList.thisMove.getX(),Global.game.moveList.thisMove.getY());
		int x = Integer.parseInt(str.substring(1))-1;
		char y = str.charAt(0);
		
		return new Point(x, alphabet.indexOf(y));
	}
}