package com.sam.hex;

import android.graphics.Point;

public class GameAction {
	public static Point hex;

	public static boolean checkWinPlayer1() {
		for (int i = 0; i < Global.gridSize; i++) {
			if (RegularPolygonGameObject.checkWinTeam((byte) 1,
					Global.gridSize, i, Global.gamePiece)) {
				System.out.println("Player one wins");
				String path=RegularPolygonGameObject.findShortestPath((byte) 1,
						Global.gridSize, i, Global.gamePiece);
				RegularPolygonGameObject.colorPath(Global.gridSize,i,path);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkWinPlayer2() {
		for (int i = 0; i < Global.gridSize; i++) {
			if (RegularPolygonGameObject.checkWinTeam((byte) 2, i,
					Global.gridSize, Global.gamePiece)) {
				System.out.println("Player Two wins");
				RegularPolygonGameObject.findShortestPath((byte) 2, i,
				Global.gridSize, Global.gamePiece);
				return true;
			}
		}
		return false;
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
			
			Move lastMove = Global.moveList.thisMove;
			Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
			Global.moveList = Global.moveList.nextMove;
			
			if(Global.currentPlayer==1) Global.player2.undoCalled();
			else Global.player1.undoCalled();
			
			if((Global.player1Type!=0 || Global.player2Type!=0) && !(Global.player1Type!=0 && Global.player2Type!=0)){
				lastMove = Global.moveList.thisMove;
				Global.gamePiece[lastMove.getX()][lastMove.getY()].setTeam((byte)0);
				Global.moveList = Global.moveList.nextMove;
				
				Global.currentPlayer = (Global.currentPlayer%2)+1;
				
				if(Global.currentPlayer==1) Global.player2.undoCalled();
				else Global.player1.undoCalled();
			}
			
			GameAction.hex = new Point(-1,-1);
			Global.moveNumber--;
			
			//Reset the game if it's already ended
			if(Global.gameOver){
				GameAction.hex = null;
				Global.moveList.replay(0);
				Global.currentPlayer = (Global.currentPlayer%2)+1;
				
				Global.gameOver=false;
				Global.game = new GameObject();
			}
		}
		
		Global.board.postInvalidate();
	}
}