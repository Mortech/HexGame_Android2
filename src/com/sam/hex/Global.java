package com.sam.hex;

public class Global{
	private static byte[][] gameboard;
	private static int n;
	private static Posn[][] polyXY;
	private static byte currentPlayer = 0;
	public static byte gameType; //0 Human v Human,1 Human v ai, 2 ai v Human, 3 ai v ai;
	public static int getN() {
		return n;
	}
	public static void setN(int n) {
		Global.n = n;
	}
	public static byte[][] getGameboard() {
		return gameboard;
	}
	public static void setGameboard(byte[][] gameboard) {
		Global.gameboard = gameboard;
	}
	public static void setGameboard(int x, int y, byte team) {
		Global.gameboard[x][y] = team;
	}
	public static Posn[][] getPolyXY() {
		return polyXY;
	}
	public static void setPolyXY(Posn[][] polyXY) {
		Global.polyXY = polyXY;
	}
	public static void setPolyXY(int x, int y, Posn posn) {
		Global.polyXY[x][y] = posn;
	}
	public static byte getCurrentPlayer(){
		return currentPlayer;
	}
	public static void setCurrentPlayer(){
		Global.currentPlayer = (byte) (currentPlayer%2+1);
	}
}