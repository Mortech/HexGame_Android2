package com.sam.hex.lan;

import java.util.ArrayList;

import android.graphics.Point;

public class LANGlobal {
	public static ArrayList<LocalNetworkObject> localObjects = new ArrayList<LocalNetworkObject>();
	public static LocalNetworkObject localPlayer;
	public static String LANipAddress;
	public final static String multicastAddress = "234.235.236.237"; 
	
	public final static int multicastPort = 4080;
	public final static int playerPort = 4081;
	public final static int challengerPort = 4082;
	
	public static Point hex;
	
	public static String playerName;
	public static int playerColor;
	public static int gridSize;
}