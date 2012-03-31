package com.sam.hex;

import android.graphics.Color;

public class Global {
	public static int gridSize = 7;
	public static int windowHeight = 600;
	public static int windowWidth = 800;
	public static RegularPolygonGameObject[][] gamePiece = new RegularPolygonGameObject[7][7];
	public static BoardView board;
	public static int gameLocation = 0;
	
	//Game
	public static int currentPlayer = 1;
	public static GameObject game;
	public static Thread gameThread;
	public static boolean gameOver = false;
	public static int moveNumber = 1;
	public static MoveList moveList = new MoveList();
	
	//Player 1
	public static PlayingEntity player1;
	public static String player1Name="Player1";
	public static byte player1Type=0;//0 Human, 1 AI
	final public static int player1DefaultColor=Color.BLUE;
	public static int player1Color=player1DefaultColor;
	
	//Player 2
	public static PlayingEntity player2;
	public static String player2Name="Player2";
	public static byte player2Type=0;//0 Human, 1 AI, 2 Local Human, 3 Internet Human;
	final public static int player2DefaultColor=Color.RED;
	public static int player2Color=player2DefaultColor;
	
	public static void set(int gridSize, int windowHeight, int windowWidth) {
		if (gridSize > 0)
			Global.gridSize = gridSize;
		if (windowHeight > 10)
			Global.windowHeight = windowHeight;
		if (windowWidth > 10)
			Global.windowWidth = windowWidth;
		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
	}
	public static void set(int gridSize, int windowHeight, int windowWidth, byte p1, byte p2){
		set(gridSize, windowHeight, windowWidth);
		if(p1>=0 && p1<=2) player1Type=p1;
		if(p1>=0 && p1<=4) player2Type=p2;
	}
}