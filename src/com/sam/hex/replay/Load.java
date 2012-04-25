package com.sam.hex.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.os.Environment;

import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.MoveList;
import com.sam.hex.PlayerObject;
import com.sam.hex.PlayingEntity;

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
    	            
    	            Global.player1.setColor((Integer) inputStream.readObject());
    				Global.player2.setColor((Integer) inputStream.readObject());
    				Global.player1.setName((String) inputStream.readObject());
    				Global.player2.setName((String) inputStream.readObject());
    				Global.moveList = (MoveList) inputStream.readObject();
    				Global.gridSize = (Integer) inputStream.readObject();
    				Global.moveNumber = (Integer) inputStream.readObject();
//    				Global.player1 = (PlayingEntity) inputStream.readObject();
//    				Global.player2 = (PlayingEntity) inputStream.readObject();
//    				if(Global.player1==null){
    					Global.player1=new PlayerObject((byte)1);
//    				}
//    				if(Global.player2==null){
    					Global.player2=new PlayerObject((byte)2);
//    				}
    				
    				inputStream.close();
    	        }
    	        catch(Exception e){
    	        	e.printStackTrace();
    	        }
				
				Global.currentPlayer=(Global.moveNumber%2)+1;
				HexGame.replay = true;
				HexGame.replayRunning = false;
				HexGame.startNewGame = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}