package com.sam.hex;

import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Global {
	public static int windowHeight = 600;
	public static int windowWidth = 800;
	public static BoardView board;
	public static TextView timerText;
	public static TextView winnerText;
	public static String winnerMsg;
	public static ImageButton replayForward;
	public static ImageButton replayPlayPause;
	public static ImageButton replayBack;
	public static RelativeLayout replayButtons;
	
	//Game
	public static GameObject game;
	public static int gameLocation;
	
	//Player 1
	public static byte player1Type=0;//0 Human, 1 AI
	final public static int player1DefaultColor=Color.BLUE;
	public static ImageButton player1Icon;
	
	//Player 2
	public static byte player2Type=0;//0 Human, 1 AI, 2 Local Human, 3 Internet Human, 4 AI;
	final public static int player2DefaultColor=Color.RED;
	public static ImageButton player2Icon;
	
//	public static void set(int gridSize, int windowHeight, int windowWidth) {
//		if (gridSize > 0)
//			Global.gridSize = gridSize;
//		if (windowHeight > 10)
//			Global.windowHeight = windowHeight;
//		if (windowWidth > 10)
//			Global.windowWidth = windowWidth;
//		gamePiece = new RegularPolygonGameObject[gridSize][gridSize];
//	}
}