package com.sam.hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.sam.hex.ai.bee.BeeGameAI;
import com.sam.hex.ai.will.GameAI;
import com.sam.hex.lan.LANMessage;
import com.sam.hex.lan.LocalLobbyActivity;
import com.sam.hex.lan.LocalPlayerObject;

public class HexGame extends Activity {
	public static boolean startNewGame = true;
	public static boolean replay = false;
	public static boolean replayRunning = false;
	public static String fileName;
	
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
    	replayRunning=false;
    	
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
    	 if(replayRunning){
     		//Do nothing
     	}
    	 else if(replay){
    		replay = false;
    		replay();
    	}
    	else if(Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type && Integer.decode(prefs.getString("player2Type", "0")) == 2){
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
        	startActivity(new Intent(getBaseContext(),FileExplore.class));
        	finish();
            return true;
        case R.id.saveReplay:
        	showSavingDialog();
        	return true;
        case R.id.quit:
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	            //Yes button clicked
        	        	Global.gameOver = true;
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
    	if(Global.game!=null && Global.game.go){
    		Global.gameOver=true;
    		Global.game.stop();
    		//Let the thread die
	    	try {
				Global.gameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	Global.gameOver=false;
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
    		else if(Global.player1Type==(byte) 4) Global.player1=new BeeGameAI(1);
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
    		else if(Global.player2Type==(byte) 4) Global.player2=new BeeGameAI(2);
    	}
    }
    
    private void undo(){
    	if(Global.player1Type==0 || Global.player2Type==0)
	    		BoardTools.undo();
    }
    
    private void newGame(){
    	if(Global.player1.newgameCalled() && Global.player2.newgameCalled()){
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
		Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
		Global.board=new BoardView(this);
    	Global.board.setOnTouchListener(new TouchListener());
	    setContentView(Global.board);
	    Global.currentPlayer=(Global.currentPlayer%2)+1;
	    replayRunning = true;
		new Thread(new Replay(), "replay").start();
    }
    
    private void saveGame(String fileName){
    	Thread saving = new Thread(new ThreadGroup("Save"), new save(), "saving", 200000);
		saving.start();
		try {
			saving.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showSavedDialog("Saved!");
	}
    
    class save implements Runnable{

		@Override
		public void run() {
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
				    	
				    	SavedGameObject savedGame = new SavedGameObject(Global.player1Color, Global.player2Color, Global.player1Name, Global.player2Name, Global.moveList, Global.gridSize, Global.moveNumber);
				    	ByteArrayOutputStream bStream = new ByteArrayOutputStream();
				    	ObjectOutputStream oStream = new ObjectOutputStream(bStream);
						oStream.writeObject(savedGame);
						byte[] data = bStream.toByteArray();
						
					    fo.write(data);
					    fo.close();
					    
					} catch (IOException e) {
						e.printStackTrace();
					}
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
	
	private void showSavingDialog(){
        final EditText editText = new EditText(Global.board.getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        editText.setText(dateFormat.format(date) + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle("Enter a filename")
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
	
	private void showSavedDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Global.board.getContext());
        builder     
        .setTitle(message)
        .setNeutralButton("Okay", null)
        .show();
    } 
}