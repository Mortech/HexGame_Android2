package com.sam.hex.replay;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.os.Environment;

import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.PlayerObject;

public class Load implements Runnable{
	@Override
	public void run() {
		try {
    		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + FileExplore.chosenFile);
    		if(file!=null){
				FileInputStream saveFile = new FileInputStream(file);
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				SavedGameObject savedGame = (SavedGameObject) restore.readObject();
				Global.player1Color = savedGame.player1Color;
				Global.player2Color = savedGame.player2Color;
				Global.player1Name = savedGame.player1Name;
				Global.player2Name = savedGame.player2Name;
				Global.moveList = savedGame.moveList;
				Global.gridSize = savedGame.gridSize;
				Global.moveNumber = savedGame.moveNumber;
				restore.close();
				
				Global.player1 = new PlayerObject((byte) 1);
				Global.player1Type = (byte) 0;
				Global.player2 = new PlayerObject((byte) 2);
				Global.player2Type = (byte) 0;
				HexGame.replay = true;
				HexGame.startNewGame = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}