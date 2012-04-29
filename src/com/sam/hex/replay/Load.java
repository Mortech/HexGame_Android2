package com.sam.hex.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.os.Environment;

import com.sam.hex.GameObject;
import com.sam.hex.HexGame;
import com.sam.hex.MoveList;
import com.sam.hex.PlayerObject;

class Load implements Runnable{	
	GameObject game;
	public Load(GameObject game){
		this.game = game;
	}
	@Override
	public void run() {
		try {
			HexGame.stopGame(game);
    		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + FileExplore.chosenFile);
    		if(file!=null){
    	        try {
    	            //Construct the ObjectInputStream object
    	        	ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));

    				int gridSize = (Integer) inputStream.readObject();
    				boolean swap = (Boolean) inputStream.readObject();

    	        	game = new GameObject(gridSize, swap);
    	        	game.player1=new PlayerObject((byte)1,game);
    	        	game.player2=new PlayerObject((byte)2,game);
    	        	
    	        	game.player1Type = (Byte) inputStream.readObject();
    	        	game.player2Type = (Byte) inputStream.readObject();
    	            game.player1.setColor((Integer) inputStream.readObject());
    				game.player2.setColor((Integer) inputStream.readObject());
    				game.player1.setName((String) inputStream.readObject());
    				game.player2.setName((String) inputStream.readObject());
    				game.moveList = (MoveList) inputStream.readObject();
    				game.moveNumber = (Integer) inputStream.readObject();
    				
    				inputStream.close();
    				
    				game.currentPlayer=(game.moveNumber%2)+1;
    				HexGame.replay = true;
    				HexGame.replayRunning = false;
    				HexGame.startNewGame = false;
    				
    				//Does not support saving PlayingEntities yet
    				game.player1Type = 0;
    	        	game.player2Type = 0;
    	        } catch(Exception e) {
    	        	e.printStackTrace();
    	        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}