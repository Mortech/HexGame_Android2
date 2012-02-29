package com.sam.hex;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import sl.shapes.*;

import 	android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.View;



public class Global{
	
	
	public static byte[][] gameboard;
	public static PlayingEntity player1;
	public static PlayingEntity player2;
	public static boolean running;
	public static int n;
	public static Posn[][] polyXY;
	public static byte currentPlayer = 1;
	public static View board;
	public static List<Posn> moveList = new ArrayList<Posn>();
	public static byte gameType = 0; //0 Human v Human,1 Human v ai, 2 ai v Human, 3 ai v ai;
	public static Posn pendingMove;
	public static int hexLength;
	
	
	public static int gridSize = 7;
	public static int windowHeight = 600;
	public static Bitmap background;
	public static int windowWidth = 800;
	public static RegularPolygonGameObject[][] gamePiece = new RegularPolygonGameObject[0][0];
	public static int playerOne=Color.BLUE;
	public static int playerTwo=Color.RED;
	public static Canvas cPolygons=new Canvas();
	public static Paint paint= new Paint();
	public static SurfaceHolder _surfaceHolder;

	//public static View drawer=new View();

	// public static Color[][] background;
	public static void set(int gS, int wH, int wW) {
		if (gS > 0)
			gridSize = gS;
		if (wH > 10)
			windowHeight = wH;
		if (wW > 10)
			windowWidth = wW;
		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
	
		background = Bitmap.createBitmap(windowWidth, windowHeight,
				Bitmap.Config.ARGB_8888);
		// background=new Color[windowWidth][windowHeight];
	}
	
	
	
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
	public static PlayingEntity getPlayer1(){
		return player1;
	}
	public static void setPlayer1(PlayingEntity player){
		Global.player1 = player;
	}
	public static PlayingEntity getPlayer2(){
		return player2;
	}
	public static void setPlayer2(PlayingEntity player){
		Global.player2 = player;
	}
	public static Posn[][] getPolyXY() {
		return polyXY;
	}
	public static boolean getRunning(){
		return running;
	}
	public static void setRunning(boolean bool){
		Global.running = bool;
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
	public static void setGameType(String type){
		int temp = Integer.decode(type);
		Global.gameType = (byte) temp;
	}
	public static Posn getPendingMove(){
		return pendingMove;
	}
	public static void setPendingMove(Posn pos){
		Global.pendingMove = pos;
	}
	public static int getHexLength(){
		return hexLength;
	}
	public static void setHexLength(int L){
		Global.hexLength = L;
	}
}