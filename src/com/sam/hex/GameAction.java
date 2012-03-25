package com.sam.hex;

import android.graphics.Point;

public class GameAction {
	private static int[][] gameboard;
	private static int n;
	private static Point[][] polyXY;
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
	
	public void setGame(int m){
		n=m;
		gameboard = new int[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				gameboard[i][j]=0;
			}
		}
		
		polyXY = new Point[n][n];
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
	
//	public boolean makeMove(int X, int Y, int team){
//    	for(int i=getN()-1;i>-1;i--){
//    		for(int j=getN()-1;j>-1;j--){
//    			if(X>getPolyXY()[i][j].x && Y>getPolyXY()[i][j].y){
//    				if(gameboard[i][j]==0){
//    					gameboard[i][j] = team;
//    					return true;
//    				}
//    				else{
//    					return false;
//    				}
//    			}
//    		}
//    	}
//    	return false;
//    }
	
	public int getN(){
		return n;
	}
	
	public int[][] getGameboard(){
		return gameboard;
	}
	
	public Point[][] getPolyXY(){
		return polyXY;
	}
	
	public void setPolyXY(int x, int y, Point cord){
		polyXY[x][y] = cord;
	}
}