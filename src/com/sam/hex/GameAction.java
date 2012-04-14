package com.sam.hex;

import com.sam.hex.lan.LANGlobal;

import android.graphics.Point;

public class GameAction {
	public static Point hex;

	public static synchronized boolean checkWinPlayer(int team) {
		if(team==1){
			for (int i = 0; i < Global.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 1, Global.gridSize, i, Global.gamePiece)) {
					System.out.println("Player one wins");
					String path=RegularPolygonGameObject.findShortestPath((byte) 1, Global.gridSize, i, Global.gamePiece);
					RegularPolygonGameObject.colorPath(Global.gridSize,i,path);
					return true;
				}
			}
			return false;
		}
		else{
			for (int i = 0; i < Global.gridSize; i++) {
				if (RegularPolygonGameObject.checkWinTeam((byte) 2, i, Global.gridSize, Global.gamePiece)) {
					System.out.println("Player two wins");
					RegularPolygonGameObject.findShortestPath((byte) 2, i, Global.gridSize, Global.gamePiece);
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
		Global.moveList.makeMove(x, y, t);
		Global.gamePiece[x][y].setTeam(t);
	}
	
	public static boolean makeMove(PlayingEntity player, byte team, Point hex){
		if(Global.gamePiece[hex.x][hex.y].getTeam() == 0){
			setTeam(team,hex.x,hex.y);
			return true;
		}
		return false;
	}
	
	public static void undo(){
		if(Global.moveNumber!=1){
			GameAction.checkedFlagReset();
			
			//Remove the piece from the board and the movelist
			Move lastMove = Global.moveList.thisMove;
			Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
			Global.moveList = Global.moveList.nextMove;
			
			//Determine who is (locally) a human
			boolean p1 = Global.player1 instanceof PlayerObject;
			boolean p2 = Global.player2 instanceof PlayerObject;
			
			if(Global.gameLocation==1){//Inside a LAN game
				if(Global.currentPlayer==1){//First player's turn
					if(LANGlobal.localPlayer.firstMove){//First player is on the network (not local)
						if(LANGlobal.undoRequested){//First player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>2){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
							}
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
							if(Global.moveNumber>2){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
							}
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
							if(Global.moveNumber>2){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
							}
						}
					}
					else{//Second player is on the network (not local)
						if(LANGlobal.undoRequested){//Second player requested the undo
							//undo twice, don't switch players
							if(Global.moveNumber>2){
								lastMove = Global.moveList.thisMove;
								Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
								Global.moveList = Global.moveList.nextMove;
							}
						}
						else{//First player requested the undo
							//undo once, switch players
							LANGlobal.hex = new Point(-1,-1);
						}
					}
				}
				
				LANGlobal.undoRequested = false;
			}
			else if(Global.currentPlayer==1 && p1){
				Global.player2.undoCalled();
				
				//If the other person isn't a (local) human
				if(!p2){
					//Undo again
					if(Global.moveNumber>2){
						lastMove = Global.moveList.thisMove;
						Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
						Global.moveList = Global.moveList.nextMove;
					}
					
					Global.player1.undoCalled();
				}
				else{
					//Otherwise, cede the turn to the other player
					GameAction.hex = new Point(-1,-1);
				}
			}
			else if(Global.currentPlayer==1 && !p1){
				Global.player1.undoCalled();
			}
			else if(Global.currentPlayer==2 && p2){
				Global.player1.undoCalled();
				
				//If the other person isn't a (local) human
				if(!p1){
					//Undo again
					if(Global.moveNumber>2){
						lastMove = Global.moveList.thisMove;
						Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
						Global.moveList = Global.moveList.nextMove;
					}
					
					Global.player2.undoCalled();
				}
				else{
					//Otherwise, cede the turn to the other player
					GameAction.hex = new Point(-1,-1);
				}
			}
			else if(Global.currentPlayer==2 && !p2){
				Global.player2.undoCalled();
			}
			
			//Reset the game if it's already ended
			if(Global.gameOver){
				Global.moveList.replay(0);
				Global.currentPlayer = (Global.moveNumber%2)+1;
				Global.moveNumber--;
				Global.game.start();
			}
			else{
				Global.moveNumber-=2;
			}
		}
		
		Global.board.postInvalidate();
	}
}