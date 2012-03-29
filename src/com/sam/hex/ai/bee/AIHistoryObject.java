package com.sam.hex.ai.bee;

import java.util.concurrent.ConcurrentHashMap;

public class AIHistoryObject{
	int[][] pieces;
	ConcurrentHashMap lookUpTable;
	
	public AIHistoryObject(int[][] pieces, ConcurrentHashMap lookUpTable) {
		this.pieces = new int[pieces.length][pieces.length];
		for(int i=0;i<pieces.length;i++){
			for(int j=0;j<pieces.length;j++){
				this.pieces[i][j] = pieces[i][j];
			}
		}
		this.lookUpTable = lookUpTable;
	}
}