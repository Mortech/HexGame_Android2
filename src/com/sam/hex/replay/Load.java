package com.sam.hex.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.os.Environment;

import com.sam.hex.GameObject;
import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.MoveList;
import com.sam.hex.PlayerObject;

class Load implements Runnable{	
	@Override
	public void run() {
		try {
			HexGame.stopGame();
    		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + FileExplore.chosenFile);
    		if(file!=null){
    	        try {
    	        	Global.game = new GameObject();
    	        	Global.game.player1=new PlayerObject((byte)1);
    	        	Global.game.player2=new PlayerObject((byte)2);
    	        	
    	            //Construct the ObjectInputStream object
    	        	ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
    	            
    	        	Global.player1Type = (Byte) inputStream.readObject();
    	        	Global.player2Type = (Byte) inputStream.readObject();
    	            Global.game.player1.setColor((Integer) inputStream.readObject());
    				Global.game.player2.setColor((Integer) inputStream.readObject());
    				Global.game.player1.setName((String) inputStream.readObject());
    				Global.game.player2.setName((String) inputStream.readObject());
    				Global.game.moveList = (MoveList) inputStream.readObject();
    				Global.game.gridSize = (Integer) inputStream.readObject();
    				Global.game.moveNumber = (Integer) inputStream.readObject();
    				
    				inputStream.close();
    				
    				Global.game.currentPlayer=(Global.game.moveNumber%2)+1;
    				HexGame.replay = true;
    				HexGame.replayRunning = false;
    				HexGame.startNewGame = false;
    				
    				//Does not support saving PlayingEntities yet
    				Global.player1Type = 0;
    	        	Global.player2Type = 0;
    	        } catch(Exception e) {
    	        	e.printStackTrace();
    	        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}