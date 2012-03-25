package com.sam.hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;

public class GameObject implements Runnable {
	Thread theGameRunner;
	boolean go=true;

	public GameObject() {
		theGameRunner = new Thread(this, "runningGame"); //Create a new thread.
		System.out.println(theGameRunner.getName());
		Global.gameRunning = true;
		theGameRunner.start(); //Start the thread.
	}
	
	public void stop(){
		go=false;
	}
	
	public void run() {
		//Loop the game
		while(go){
			if (Global.currentPlayer == 1) {
				Global.player1.getPlayerTurn();
				if (GameAction.checkWinPlayer1()){
					announceWinner(1);
					go=false;
				}
				
				Global.currentPlayer=(Global.currentPlayer%2)+1;
			}
			else {
				Global.player2.getPlayerTurn();
				if (GameAction.checkWinPlayer2()){
					announceWinner(2);
					go=false;
				}
				
				Global.currentPlayer=(Global.currentPlayer%2)+1;
			}
			GameAction.checkedFlagReset();
			Global.moveNumber += 1;
			Global.board.postInvalidate();
		}
	}
	
	public static void announceWinner(int team){
		Global.gameRunning = false;
		Global.board.postInvalidate();
		Looper.prepare();
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Okay button clicked
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //Save button clicked
    	        	showInputDialog("Enter a filename");
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
    	if(team==1){
    		builder.setMessage(Global.player1Name+" wins!");
		}
		else{
			builder.setMessage(Global.player2Name+" wins!");
		}
    	builder.setPositiveButton("Okay", dialogClickListener).setNegativeButton("Save", dialogClickListener).show();
		Looper.loop();
	}
	
	private static void saveGame(String fileName){
		createDirIfNoneExists(File.separator + "Hex" + File.separator);
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName);
		if(file!=null){
			String filePath = file.getPath();
			if(!filePath.toLowerCase().endsWith(".rhex")){
			    file = new File(filePath + ".rhex");
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(file.exists()){
				try {
			    	OutputStream fo = new FileOutputStream(file);
			    	
			    	SavedGameObject savedGame = new SavedGameObject(Global.player1Color, Global.player2Color, Global.player1Name, Global.player2Name, Global.moveList, Global.gridSize);
			    	ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			    	ObjectOutputStream oStream = new ObjectOutputStream(bStream);
					oStream.writeObject(savedGame);
					byte[] data = bStream.toByteArray();
					
				    fo.write(data);
				    fo.close();
				    
				    showSavedDialog("Saved!");
				} catch (IOException e) {
					e.printStackTrace();
					showSavedDialog("Couldn't save.");
				}
			}
		}
	}
	
	public static boolean createDirIfNoneExists(String path) {
	    boolean ret = true;

	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	static String fileName;
	public static void showInputDialog(final String message){
        final EditText editText = new EditText(Global.board.getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        editText.setText(dateFormat.format(date) + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle(message)
        .setView(editText)
        .setPositiveButton("OK", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			fileName = editText.getText().toString();
    			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName);
    			String filePath = file.getPath();
    			if(!filePath.toLowerCase().endsWith(".rhex")){
    			    file = new File(filePath + ".rhex");
    			}
    			saveGame(fileName);
    		}
        })
        .setNegativeButton("Cancel", null)
        .show();
    }
	
	public static void showSavedDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle(message)
        .setNeutralButton("Okay", null)
        .show();
    } 
}