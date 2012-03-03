package com.sam.hex;

import java.awt.Shape; 
import android.graphics.Color;
import android.graphics.Bitmap;

public class Global {
	public static int gridSize = 7;
	public static int windowHeight = 600;
	public static Bitmap background;
	public static int windowWidth = 800;
	public static RegularPolygonGameObject[][] gamePiece = new RegularPolygonGameObject[0][0];
	public static Shape[][] hexes = gamePiece;
	public static int playerOne=Color.BLUE;
	public static int playerTwo=Color.RED;
	public static byte gameType; //0 Human v Human,1 Human v ai, 2 ai v Human, 3 ai v ai;

	// public static Color[][] background;
	public static void set(int gS, int wH, int wW) {
		if (gS > 0)
			gridSize = gS;
		if (wH > 10)
			windowHeight = wH;
		if (wW > 10)
			windowWidth = wW;
		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
		hexes = gamePiece;
		background=Bitmap.createBitmap(windowWidth, windowHeight,Bitmap.Config.ARGB_8888);
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