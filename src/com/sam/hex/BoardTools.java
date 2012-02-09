package com.sam.hex;

public class BoardTools{
	public static void setGame(int m){
		Global.setN(m);
		Global.setGameboard(new byte[Global.getN()][Global.getN()]);
		for(int i=0;i<Global.getN();i++){
			for(int j=0;j<Global.getN();j++){
				Global.getGameboard()[i][j]=0;
			}
		}
		
		Global.setPolyXY(new Posn[Global.getN()][Global.getN()]);
	}
	
	public static void makeMove(int x, int y, byte team){
		if(Global.getGameboard()[x][y]==0){
			Global.setGameboard(x,y,team);
		}
	}
	
	public static int getN(){
		return Global.getN();
	}
	
	public static byte[][] teamGrid(){
		return Global.getGameboard();
	}
	
	public static Posn[][] getPolyXY(){
		return Global.getPolyXY();
	}
	
	public static void setPolyXY(int x, int y, Posn cord){
		Global.setPolyXY(x, y, cord);
	}
}