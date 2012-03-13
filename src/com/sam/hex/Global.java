package com.sam.hex;

import android.graphics.Color;
import android.graphics.Point;
import java.util.ArrayList;

public class Global {
	public static int gridSize = 7;
	public static int windowHeight = 600;
	public static int windowWidth = 800;
	public static RegularPolygonGameObject[][] gamePiece = new RegularPolygonGameObject[7][7];
	public static BoardView board;
	public static ArrayList<Point> moveList=new ArrayList<Point>();
	//Game
	public static byte currentPlayer = 1;
	public static GameObject game;
	
	//AI
	public static int difficulty=1;
	public static boolean slowAI=true;
	
	//Player 1
	public static PlayingEntity player1;
	public static String player1Name="Player1";
	public static byte player1Type=0;//0 Human, 1 AI
	public static int player1Color=Color.BLUE;
	final public static int player1DefaultColor=Color.BLUE;
	
	//Player 2
	public static PlayingEntity player2;
	public static String player2Name="Player2";
	public static byte player2Type=0;//0 Human, 1 AI, 2 Local Human, 3 Internet Human;
	public static int player2Color=Color.RED;
	final public static int player2DefaultColor=Color.RED;
	
	public static void set(int gS, int wH, int wW) {
		if (gS > 0)
			gridSize = gS;
		if (wH > 10)
			windowHeight = wH;
		if (wW > 10)
			windowWidth = wW;
		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
	}
	public static void set(int gS, int wH, int wW, byte p1, byte p2){
		set(gS, wH, wW);
		if(p1>=0 && p1<=2) player1Type=p1;
		if(p1>=0 && p1<=4) player2Type=p2;
	}
}