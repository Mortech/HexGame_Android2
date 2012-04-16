package com.sam.hex.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.os.Environment;

import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.MoveList;
import com.sam.hex.PlayerObject;

public class Load implements Runnable{
	@Override
	public void run() {
		try {
			HexGame.stopGame();
    		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + FileExplore.chosenFile);
    		if(file!=null){
    	        try {
    	            //Construct the ObjectInputStream object
    	        	ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
    	            
    	            Global.player1Color = (Integer) inputStream.readObject();
    				Global.player2Color = (Integer) inputStream.readObject();
    				Global.player1Name = (String) inputStream.readObject();
    				Global.player2Name = (String) inputStream.readObject();
    				Global.moveList = (MoveList) inputStream.readObject();
    				Global.gridSize = (Integer) inputStream.readObject();
    				Global.moveNumber = (Integer) inputStream.readObject();
    				
    				inputStream.close();
    	        }
    	        catch(Exception e){
    	        	e.printStackTrace();
    	        }
				
				Global.currentPlayer=(Global.moveNumber%2)+1;
				Global.player1 = new PlayerObject((byte) 1);
				Global.player1Type = (byte) 0;
				Global.player2 = new PlayerObject((byte) 2);
				Global.player2Type = (byte) 0;
				HexGame.replay = true;
				HexGame.replayRunning = false;
				HexGame.startNewGame = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}