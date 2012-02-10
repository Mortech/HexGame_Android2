package com.sam.hex;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class Global{
	private static byte[][] gameboard;
	private static int n;
	private static Posn[][] polyXY;
	private static byte currentPlayer = 1;
	private static View board;
	private static List<Posn> moveList = new ArrayList<Posn>();
	private static byte gameType = 1; //0 Human v Human,1 Human v ai, 2 ai v Human, 3 ai v ai;
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
	public static void setCurrentPlayer(byte player){
		Global.currentPlayer = player;
	}
	public static View getBoard(){
		return board;
	}
	public static void setBoard(View view){
		Global.board = view;
	}
	public static List<Posn> getMoveList(){
		return moveList;
	}
	public static void setMoveList(List<Posn> list){
		Global.moveList = list;
	}
	public static void addToMoveList(Posn pos){
		Global.moveList.add(pos);
	}
	public static void removeFromMoveList(Posn pos){
		Global.moveList.remove(pos);
	}
	public static void clearMoveList(){
		Global.moveList.clear();
	}
	public static byte getGameType(){
		return gameType;
	}
	public static void setGameType(byte type){
		Global.gameType = type;
	}
}