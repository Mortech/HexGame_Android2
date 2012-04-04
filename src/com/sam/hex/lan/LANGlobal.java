package com.sam.hex.lan;

import java.util.ArrayList;

public class LANGlobal {
	public static ArrayList<LocalNetworkObject> localObjects = new ArrayList<LocalNetworkObject>();
	public static LocalNetworkObject localPlayer;
	public static String LANipAddress;
	public final static String multicastAddress = "234.235.236.237"; 
	public final static int port = 4080;
	public final static int unicastPort = 4081;
	public final static int gamePort = 4082;
	public final static int playerPort = 4083;
	public final static int newgamePort = 4084;
	public final static int undoPort = 4085;
	public final static int challengerPort = 4086;
}