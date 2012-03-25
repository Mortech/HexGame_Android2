package com.sam.hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.graphics.Point;

import com.sam.hex.lan.LANMessage;
import com.sam.hex.lan.LocalLobbyActivity;
import com.sam.hex.lan.LocalPlayerObject;

public class HexGame extends Activity {
	public static boolean startNewGame = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(HexGame.startNewGame){
        	initializeNewGame();//Must be set up immediately
        }
        else{
        	//Readd the view
        	Global.board=new BoardView(this);
        	Global.board.setOnTouchListener(new TouchListener());
    	    setContentView(Global.board);
        }
    }
    
    class TouchListener implements OnTouchListener{
    	public boolean onTouch(View v, MotionEvent event){
    		int x = (int)event.getX();
			int y = (int)event.getY();
			for(int xc = 0; xc < Global.gamePiece.length; xc++){
				for(int yc=0; yc<Global.gamePiece[0].length; yc++)
					if(Global.gamePiece[xc][yc].contains(x, y)){
						if(Global.game!=null)GameAction.setPiece(new Point(xc,yc));
						//Return false. We got our point. (True is used for gestures)
						return false;
					}
			}
			//Return false. We got our point. (True is used for gestures)
			return false;
    	}
    }
    
    private void initializeNewGame(){
    	startNewGame = false;
    	
    	//Stop the old game
    	stopGame();
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Return to the first player
    	Global.currentPlayer = 1;
    	
    	//Set game mode
    	Global.player1Type=(byte)Integer.parseInt(prefs.getString("player1Type", "0"));
    	Global.player2Type=(byte)Integer.parseInt(prefs.getString("player2Type", "0"));
    	
    	//Set player names
    	setNames(prefs);
    	
    	//Set player colors
    	setColors(prefs);
    	
    	//Create our board
    	setGrid(prefs);
    	GameAction.hex = null;
    	Global.moveNumber = 1;
    	Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
    	Global.board=new BoardView(this);
    	Global.board.setOnTouchListener(new TouchListener());
	    setContentView(Global.board);
    	
    	//Make sure the board is empty and defaults are set
    	Global.moveList=new MoveList();
    	
    	//Set up player1
		setPlayer1();
		
		//Set up player2
		setPlayer2();
    	
        //Create the game object
        Global.game = new GameObject();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	System.out.println("Resuming");
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Check if settings were changed and we need to run a new game
    	if(Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type && Integer.decode(prefs.getString("player2Type", "0")) == 2){
    		//Go to the local lobby
    		Global.player2Type = 2;
        	startActivity(new Intent(getBaseContext(),LocalLobbyActivity.class));
        	finish();
    	}
    	else if(Integer.decode(prefs.getString("player2Type", "0")) == 2){
    		//We're in an existing local game
    		
    		//Check if the color changed
    		if(Global.player1 instanceof PlayerObject && Global.player1Color != prefs.getInt("player1Color", Global.player1DefaultColor)){
    			setColors(prefs);
    			new LANMessage("I changed my color to "+Global.player1Color, Global.localPlayer.ip, 4080);
    		}
    		else if(Global.player2 instanceof PlayerObject && Global.player2Color != prefs.getInt("player2Color", Global.player2DefaultColor)){
    			setColors(prefs);
    			new LANMessage("I changed my color to "+Global.player2Color, Global.localPlayer.ip, 4080);
    		}
    		
    		//Check if the name changed
    		if(Global.player1 instanceof PlayerObject && !Global.player1Name.equals(prefs.getString("player1Name", Global.player1Name))){
    			setNames(prefs);
    			new LANMessage("I changed my name to "+Global.player1Name, Global.localPlayer.ip, 4080);
    		}
    		else if(Global.player2 instanceof PlayerObject && !Global.player2Name.equals(prefs.getString("player2Name", Global.player2Name))){
    			setNames(prefs);
    			new LANMessage("I changed my name to "+Global.player2Name, Global.localPlayer.ip, 4080);
    		}
    		Global.board.invalidate();
    	}
    	else if(HexGame.startNewGame){
    		initializeNewGame();
    	}
    	else if(somethingChanged(prefs)){
    		//Reset the game
    		initializeNewGame();
    	}
    	else{
    		//Apply minor changes without stopping the current game
    		
    		//Reset the colors for every piece
    		if(Global.player1Color != prefs.getInt("player1Color", Global.player1DefaultColor)){
    			for(int x=0;x<Global.gridSize;x++){
    				for(int y=0;y<Global.gridSize;y++){
    					if(Global.gamePiece[x][y].getColor()==Global.player1Color){
    						Global.gamePiece[x][y].setColor(prefs.getInt("player1Color", Global.player1DefaultColor));
    					}
    				}
    			}
    			Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
    		}
    		if(Global.player2Color != prefs.getInt("player2Color", Global.player2DefaultColor)){
    			for(int x=0;x<Global.gridSize;x++){
    				for(int y=0;y<Global.gridSize;y++){
    					if(Global.gamePiece[x][y].getColor()==Global.player2Color){
    						Global.gamePiece[x][y].setColor(prefs.getInt("player2Color", Global.player2DefaultColor));
    					}
    				}
    			}
    			Global.player2Color = prefs.getInt("player2Color", Global.player2DefaultColor);
    		}
    		
    		//Reset the players names
	    	setNames(prefs);
	    	
	    	//Reset the background colors
	    	Global.board.onSizeChanged(Global.windowWidth,Global.windowHeight,0,0);
	    	
	    	//Apply everything
	    	Global.board.invalidate();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.settings:
        	startActivity(new Intent(getBaseContext(),Preferences.class));
            return true;
        case R.id.undo:
        	undo();
            return true;
        case R.id.newgame:
        	newGame();
            return true;
        case R.id.replay:
        	replay();
            return true;
        case R.id.loadReplay:
        	showInputDialog("Enter the filename of a saved game");
            return true;
        case R.id.quit:
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	            //Yes button clicked
        	        	Global.gameRunning = false;
        	        	android.os.Process.killProcess(android.os.Process.myPid());
        	            break;
        	        case DialogInterface.BUTTON_NEGATIVE:
        	            //No button clicked
        	        	//Do nothing
        	            break;
        	        }
        	    }
        	};

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	//If the board's empty, just trigger "startNewGame"
    	for(int x=0;x<Global.gridSize;x++){
			for(int y=0;y<Global.gridSize;y++){
				if(Global.gamePiece[x][y].getTeam()==(byte)1 || Global.gamePiece[x][y].getTeam()==(byte)2){
					return;
				}
			}
		}
    	HexGame.startNewGame=true;
    }
    
    private void stopGame(){
    	if(Global.game!=null){
    		Global.gameRunning=false;
    		Global.game.stop();
    		//Let the thread die
	    	try {
				Thread.sleep(110);
			}
	    	catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	Global.gameRunning=true;
    	}
    }
    
    /**
     * Refreshes both player's names
     * Does not invalidate the board
     * */
    public static void setNames(SharedPreferences prefs){
    	if(Global.player2Type==(byte)2){
    		//Playing over LAN
    		if(Global.localPlayer.firstMove){
    			Global.player1Name = Global.localPlayer.playerName;
        		Global.player2Name = prefs.getString("player1Name", "Player1");
    		}
    		else{
    			Global.player1Name = prefs.getString("player1Name", "Player1");
        		Global.player2Name = Global.localPlayer.playerName;
    		}
    	}
    	else{
    		//Playing on the same phone
    		Global.player1Name = prefs.getString("player1Name", "Player1");
    		Global.player2Name = prefs.getString("player2Name", "Player2");
    	}
    }
    
    /**
     * Refreshes both player's colors
     * Does not invalidate the board
     * */
    public static void setColors(SharedPreferences prefs){
    	if(Global.player2Type==(byte)2){
    		//Playing over LAN
    		if(Global.localPlayer.firstMove){
    			Global.player1Color = Global.localPlayer.playerColor;
        		Global.player2Color = prefs.getInt("player1Color", Global.player1DefaultColor);
    		}
    		else{
    			Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
        		Global.player2Color = Global.localPlayer.playerColor;
    		}
    	}
    	else{
    		//Playing on the same phone
    		Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
    		Global.player2Color = prefs.getInt("player2Color", Global.player2DefaultColor);
    	}
    }
    
    private void setGrid(SharedPreferences prefs){
    	if(Global.player2Type==(byte)2){
    		//Playing over LAN
    		if(Global.localPlayer.firstMove){
    			Global.gridSize=Global.localPlayer.gridSize;
    		}
    		else{
    			Global.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    		}
    	}
    	else{
    		//Playing on the same phone
    		Global.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    		if(Global.gridSize==0) Global.gridSize=Integer.decode(prefs.getString("customGameSizePref", "7"));
    	}
    	
    	//We don't want 0x0 games
    	if(Global.gridSize<=0) Global.gridSize=1;
    }
    
    private void setPlayer1(){
    	if(Global.player2Type==(byte)2){
    		//Playing over LAN
    		if(Global.localPlayer.firstMove){
    			Global.player1=new LocalPlayerObject((byte)1);
    		}
    		else{
    			if(Global.player1Type==(byte) 0) Global.player1=new PlayerObject((byte)1);
        		else if(Global.player1Type==(byte) 1) Global.player1=new GameAI((byte)1,(byte)1);
    		}
    	}
    	else{
    		//Playing on the same phone
    		if(Global.player1Type==(byte) 0) Global.player1=new PlayerObject((byte)1);
    		else if(Global.player1Type==(byte) 1) Global.player1=new GameAI((byte)1,(byte)1);
    	}
    }
    
    private void setPlayer2(){
    	if(Global.player2Type==(byte)2){
    		//Playing over LAN
    		if(Global.localPlayer.firstMove){
    			if(Global.player1Type==(byte) 0) Global.player2=new PlayerObject((byte)2);
    			else if(Global.player1Type==(byte) 1) Global.player2=new GameAI((byte)2,(byte)1);
    		}
    		else{
    			Global.player2=new LocalPlayerObject((byte)2);
    		}
    	}
    	else{
    		//Playing on the same phone
    		if(Global.player2Type==(byte) 0) Global.player2=new PlayerObject((byte)2);
    		else if(Global.player2Type==(byte) 1) Global.player2=new GameAI((byte)2,(byte)1);
    	}
    }
    
    private void undo(){
    	//TODO Ask other player if it's okay to undo
    	if(Global.player1 instanceof LocalPlayerObject || Global.player2 instanceof LocalPlayerObject){
    		//Request permission
    	}
    	else{
    		if(Global.player1Type==0 || Global.player2Type==0)
	    		BoardTools.undo();
    	}
    }
    
    private void newGame(){
    	//TODO Ask other player if they'd like to play a new game
    	if(Global.player1 instanceof LocalPlayerObject || Global.player2 instanceof LocalPlayerObject){
    		//Request permission
    	}
    	else{
    		initializeNewGame();
    		Global.board.invalidate();
    	}
    }
    
    /**
     * Returns the context for the current game
     * */
    public static Context getContext(){
    	return Global.board.getContext();
    }
    
    /**
     * Returns true if a major setting was changed
     * */
    public static boolean somethingChanged(SharedPreferences prefs){
    	return (Integer.decode(prefs.getString("gameSizePref", "7")) != Global.gridSize && Integer.decode(prefs.getString("gameSizePref", "7")) != 0) 
    			|| (Integer.decode(prefs.getString("customGameSizePref", "7")) != Global.gridSize && Integer.decode(prefs.getString("gameSizePref", "7")) == 0)
    			|| Integer.decode(prefs.getString("player1Type", "0")) != (int) Global.player1Type 
    			|| Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type;
    }
    
    private void replay(){
    	if(Global.moveNumber>1){
    		Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
    		Global.board=new BoardView(this);
        	Global.board.setOnTouchListener(new TouchListener());
    	    setContentView(Global.board);
    	    Global.currentPlayer=(Global.currentPlayer%2)+1;
    		new Thread(new Replay(), "replay").start();
    	}
    }
    
    private void load(String fileName){
    	try {
    		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName);
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
				Global.moveNumber = 2;
				restore.close();
				
				Global.board.onSizeChanged(Global.windowWidth,Global.windowHeight,0,0);
				Global.board.invalidate();
				
				replay();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
    }
    
	public void showInputDialog(final String message){
        final EditText editText = new EditText(Global.board.getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle(message)
        .setView(editText)
        .setPositiveButton("OK", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			String fileName = editText.getText().toString();
    			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Hex" + File.separator + fileName);
    			String filePath = file.getPath();
    			if(!filePath.toLowerCase().endsWith(".rhex")){
    			    file = new File(filePath + ".rhex");
    			}
    			if(!file.exists()) showInputDialog("That file doesnt exist");
    			else load(fileName);
    		}
        })
        .setNegativeButton("Cancel", null)
        .show();
    }
}