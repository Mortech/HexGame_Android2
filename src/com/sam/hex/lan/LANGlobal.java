package com.sam.hex.lan;

import java.util.ArrayList;

public class LANGlobal {
	public static ArrayList<LocalNetworkObject> localObjects = new ArrayList<LocalNetworkObject>();
	public static LocalNetworkObject localPlayer;
	public static String LANipAddress;
	public final static int port = 4080;
}