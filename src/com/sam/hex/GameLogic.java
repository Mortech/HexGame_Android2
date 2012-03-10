package com.sam.hex;
//TODO Merge class into GameAction. This class shouldn't even exist.
public class GameLogic{
	private static int[][] gameboard;
	private static int n;
	private static Posn[][] polyXY;
	
	public void setGame(int m){
		n=m;
		gameboard = new int[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				gameboard[i][j]=0;
			}
		}
		
		polyXY = new Posn[n][n];
	}
	
	public boolean makeMove(int X, int Y, int team){
    	for(int i=getN()-1;i>-1;i--){
    		for(int j=getN()-1;j>-1;j--){
    			if(X>getPolyXY()[i][j].getX() && Y>getPolyXY()[i][j].getY()){
    				if(gameboard[i][j]==0){
    					gameboard[i][j] = team;
    					return true;
    				}
    				else{
    					return false;
    				}
    			}
    		}
    	}
    	return false;
    }
	
	public int getN(){
		return n;
	}
	
	public int[][] getGameboard(){
		return gameboard;
	}
	
	public Posn[][] getPolyXY(){
		return polyXY;
	}
	
	public void setPolyXY(int x, int y, Posn cord){
		polyXY[x][y] = cord;
	}
}