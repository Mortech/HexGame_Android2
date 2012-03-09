package com.sam.hex;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import java.util.ArrayList;

public class Global {
	public static int gridSize = 7;
	public static int windowHeight = 600;
	public static int windowWidth = 800;
	public static RegularPolygonGameObject[][] gamePiece = new RegularPolygonGameObject[7][7];
	public static String playerOneName="Player1";
	final public static int playerOneDefalt=Color.BLUE;
	public static int playerOne=Color.BLUE;
	public static String playerTwoName="Player2";
	final public static int playerTwoDefalt=Color.RED;
	public static int playerTwo=Color.RED;
	public static byte gameType; //0 Human v Human,1 Human v ai, 2 ai v Human, 3 ai v ai;
	public static BoardView board;
	public static ArrayList<Point> moveList=new ArrayList<Point>();
	public static int difficulty=1;
	public static boolean slowAI=true;
	static SharedPreferences gamePrefs;

	

	// public static Color[][] background;
	public static void set(int gS, int wH, int wW) {
		if (gS > 0)
			gridSize = gS;
		if (wH > 10)
			windowHeight = wH;
		if (wW > 10)
			windowWidth = wW;
		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
		// background=new Color[windowWidth][windowHeight];
	}
	public static void set(int gS, int wH, int wW, byte AI){
		set(gS, wH, wW);
		if (gameType < 4)
		gameType=AI;
	}
	// public static int windowHeight=200;
	// public static int windowWidth=400;
}