package com.sam.hex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.graphics.Point;

import com.sam.hex.ai.bee.BeeGameAI;
import com.sam.hex.ai.will.GameAI;
import com.sam.hex.lan.LANGlobal;
import com.sam.hex.lan.LANMessage;
import com.sam.hex.lan.LocalLobbyActivity;
import com.sam.hex.lan.LocalPlayerObject;
import com.sam.hex.replay.FileExplore;
import com.sam.hex.replay.Replay;
import com.sam.hex.replay.Save;

public class HexGame extends Activity {
	public static boolean startNewGame = true;
	public static boolean replay = false;
	public static boolean replayRunning = false;
	
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
    	
    	setPlayer1();
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
    		replay(800);
    	}
    	else if(Integer.decode(prefs.getString("gameLocation", "0")) != Global.gameLocation && Integer.decode(prefs.getString("gameLocation", "0")) == 1){
    		//Go to the local lobby
    		Global.player2Type = 2;
        	startActivity(new Intent(getBaseContext(),LocalLobbyActivity.class));
        	finish();
    	}
    	else if(Global.gameLocation == 1){
    		//We're in an existing local game
    		
    		//Check if the color changed
    		if(Global.player1 instanceof PlayerObject && Global.player1Color != prefs.getInt("player1Color", Global.player1DefaultColor)){
    			setColors(prefs);
    			new LANMessage("I changed my color to "+Global.player1Color, LANGlobal.localPlayer.ip, 4080);
    		}
    		else if(Global.player2 instanceof PlayerObject && Global.player2Color != prefs.getInt("player2Color", Global.player2DefaultColor)){
    			setColors(prefs);
    			new LANMessage("I changed my color to "+Global.player2Color, LANGlobal.localPlayer.ip, 4080);
    		}
    		
    		//Check if the name changed
    		if(Global.player1 instanceof PlayerObject && !Global.player1Name.equals(prefs.getString("player1Name", Global.player1Name))){
    			setNames(prefs);
    			new LANMessage("I changed my name to "+Global.player1Name, LANGlobal.localPlayer.ip, 4080);
    		}
    		else if(Global.player2 instanceof PlayerObject && !Global.player2Name.equals(prefs.getString("player2Name", Global.player2Name))){
    			setNames(prefs);
    			new LANMessage("I changed my name to "+Global.player2Name, LANGlobal.localPlayer.ip, 4080);
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
        	replayRunning = false;
        	startActivity(new Intent(getBaseContext(),Preferences.class));
            return true;
        case R.id.undo:
        	undo();
            return true;
        case R.id.newgame:
        	newGame();
            return true;
        case R.id.replay:
        	replay(900);
            return true;
        case R.id.loadReplay:
        	startActivity(new Intent(getBaseContext(),FileExplore.class));
        	finish();
            return true;
        case R.id.saveReplay:
        	Save save = new Save();
        	save.showSavingDialog();
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
    	System.out.println(Global.moveNumber);
    	if(Global.moveNumber==1) HexGame.startNewGame=true;
    }
    
    public static void stopGame(){
    	if(Global.gameThread!=null){
    		Global.gameOver=true;
    		Global.game.stop();
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
    	if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			Global.player1Name = LANGlobal.localPlayer.playerName;
        		Global.player2Name = prefs.getString("player1Name", "Player1");
    		}
    		else{
    			Global.player1Name = prefs.getString("player1Name", "Player1");
        		Global.player2Name = LANGlobal.localPlayer.playerName;
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
    	if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			Global.player1Color = LANGlobal.localPlayer.playerColor;
        		Global.player2Color = prefs.getInt("player1Color", Global.player1DefaultColor);
    		}
    		else{
    			Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
        		Global.player2Color = LANGlobal.localPlayer.playerColor;
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
    		if(LANGlobal.localPlayer.firstMove){
    			Global.gridSize=LANGlobal.localPlayer.gridSize;
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
    
    private void undo(){
    	if(Global.player1.supportsUndo() && Global.player2.supportsUndo())
	    		GameAction.undo();
    }
    
    private void newGame(){
    	if(Global.gameLocation==1){
    		//Inside a LAN game
    		new LANMessage("Want to play a new game?", LANGlobal.localPlayer.ip, LANGlobal.port);
    	}
    	if(Global.player1.supportsNewgame() && Global.player2.supportsNewgame()){
			if(replayRunning){
				replayRunning = false;
				try {
					replayThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			initializeNewGame();
			Global.board.invalidate();
    	}
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
    
    Thread replayThread;
    private void replay(int time){
    	//Create our board
    	GameAction.hex = null;
    	Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
    	Global.board=new BoardView(this);
    	Global.board.setOnTouchListener(new TouchListener());
	    setContentView(Global.board);
        
    	if(Global.moveNumber>1) Global.currentPlayer=(Global.currentPlayer%2)+1;
	    
    	replayRunning = true;
		replayThread = new Thread(new Replay(time), "replay");
		replayThread.start();
    }
    
    private void setPlayer1(){
    	if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			Global.player1=new LocalPlayerObject((byte)1);
    		}
    		else{
    			if(Global.player1Type==(byte) 0) Global.player1=new PlayerObject((byte)1);
        		else if(Global.player1Type==(byte) 1) Global.player1=new GameAI((byte)1,(byte)1);
        		else if(Global.player1Type==(byte) 4) Global.player1=new BeeGameAI(1);
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
    	if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			if(Global.player1Type==(byte) 0) Global.player2=new PlayerObject((byte)2);
    			else if(Global.player1Type==(byte) 1) Global.player2=new GameAI((byte)2,(byte)1);
    			else if(Global.player1Type==(byte) 4) Global.player2=new BeeGameAI(1);
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
}