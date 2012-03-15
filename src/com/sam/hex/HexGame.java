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
import java.util.ArrayList;

import com.sam.hex.lan.LocalLobbyActivity;
import com.sam.hex.lan.LocalPlayerObject;

public class HexGame extends Activity {
	public static boolean gameRunning = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(Global.gamePiece[0][0]==null){
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
						if(Global.game!=null)Global.game.setPiece(new Point(xc,yc));
						//Return false. We got our point. (True is used for gestures)
						return false;
					}
			}
			//Return false. We got our point. (True is used for gestures)
			return false;
    	}
    }
    
    public void initializeNewGame(){
    	//Stop the old game
    	if(Global.game!=null){
    		Global.game.stop();
    		//Let the thread die
	    	try {
				Thread.sleep(110);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Set game mode
    	Global.player1Type=(byte)Integer.parseInt(prefs.getString("player1Type", "0"));
    	Global.player2Type=(byte)Integer.parseInt(prefs.getString("player2Type", "0"));
    	
    	//Set player names
    	Global.player1Name = prefs.getString("player1Name", "Player1");
    	if(Global.player1 instanceof LocalPlayerObject) Global.player2Name = Global.localPlayer.toString();
    	else Global.player2Name = prefs.getString("player2Name", "Player2");
    	
    	//Set player colors
    	Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
    	Global.player2Color = prefs.getInt("player2Color", Global.player2DefaultColor);
    	
    	//Create our board
    	Global.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    	Global.difficulty=Integer.decode(prefs.getString("aiPref", "1"));
    	Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
    	BoardTools.clearBoard(); 
    	Global.board=new BoardView(this);
    	Global.board.setOnTouchListener(new TouchListener());
	    setContentView(Global.board);
    	
    	//Make sure the board is empty and defaults are set
    	Global.moveList=new ArrayList<Point>();
    	BoardTools.setBoard();
    	
    	//Set up player1
		if(Global.player1Type==(byte) 0) Global.player1=new PlayerObject((byte)1);
		else if(Global.player1Type==(byte) 1) Global.player1=new GameAI((byte)1,(byte)1);
		
		//Set up player2
		if(Global.player2Type==(byte) 0) Global.player2=new PlayerObject((byte)2);
		else if(Global.player2Type==(byte) 1) Global.player2=new GameAI((byte)2,(byte)1);
		else if(Global.player2Type==(byte) 2) Global.player2=new LocalPlayerObject((byte)2);
		else if(Global.player2Type==(byte) 3) Global.player2=new LocalPlayerObject((byte)2);
    	
        //Create the game object
        Global.game = new GameObject();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Check if settings were changed and we need to run a new game
    	if(!gameRunning){
    		initializeNewGame();
    	}
    	else if(Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type && Integer.decode(prefs.getString("player2Type", "0")) == 2){
    		//Go to the local lobby
    		Global.player2Type = 2;
        	startActivity(new Intent(getBaseContext(),LocalLobbyActivity.class));
        	finish();
    	}
    	else if(Integer.decode(prefs.getString("aiPref", "1")) != Global.difficulty || Integer.decode(prefs.getString("gameSizePref", "7")) != Global.gridSize || Integer.decode(prefs.getString("player1Type", "0")) != (int) Global.player1Type || Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type){
    		//Reset the game
    		initializeNewGame();
    	}
    	else
    	{
    		//Apply minor changes without stopping the current game
    		Global.player1Color = prefs.getInt("player1Color", Global.player1DefaultColor);
	    	Global.player2Color = prefs.getInt("player2Color", Global.player2DefaultColor);
	    	Global.player1Name = prefs.getString("player1Name", "Player1");
	    	Global.player2Name = prefs.getString("player2Name", "Player2");
	    	Global.board.onSizeChanged(Global.windowWidth,Global.windowHeight,0,0);
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
        	if(Global.player1Type==0 || Global.player2Type==0)
        		BoardTools.undo();
        	if((Global.player1Type!=0 || Global.player2Type!=0) && !(Global.player1Type!=0 && Global.player2Type!=0))
        		BoardTools.undo();
            return true;
        case R.id.newgame:
        	initializeNewGame();
        	Global.board.invalidate();
            return true;
        case R.id.quit:
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	            //Yes button clicked
        	        	gameRunning = false;
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
}
