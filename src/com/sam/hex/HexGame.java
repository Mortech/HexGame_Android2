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

public class HexGame extends Activity {
	GameObject game;
	TouchListener touchListener=new TouchListener();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(Global.gamePiece[0][0]==null){
        	initializeNewGame();//Must be set up immediately
        }
        else{
        	Global.board=new BoardView(this);
        	//Add the touch listener
    		//touchListener = new TouchListener();
            Global.board.setOnTouchListener(touchListener);
            setContentView(Global.board);
        }
    }
    class TouchListener implements OnTouchListener{
    	public boolean onTouch(View v, MotionEvent event){
    		int x = (int)event.getX();
			int y = (int)event.getY();
			for (int xc = 0; xc < Global.gamePiece.length; xc++) {
				for (RegularPolygonGameObject hex : Global.gamePiece[xc])
					if (hex.contains(x, y)) {
						if(game!=null)game.setPiece(hex); //TODO: send point instead of hex!
						return true;
					}
			}
			return true; //or maybe false?
    	}
    }
    
    public void initializeNewGame(){
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if(game!=null)
    		game.stop();
    	//Create our board
    	Global.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    	Global.difficulty=Integer.decode(prefs.getString("aiPref", "1"));
    	Global.gamePiece=new RegularPolygonGameObject[Global.gridSize][Global.gridSize];
    	BoardTools.clearBoard(); 
    	Global.board=new BoardView(this);
    	
    	//Make sure the board is empty and defaults are set
    	Global.moveList=new ArrayList<Point>();
    	BoardTools.setBoard();
    	
    	//Set game mode
    	Global.gameType=(byte)Integer.parseInt(prefs.getString("gameModePref", "0"));
		
		//Create the game object
    	
		
		
		//Add the touch listener
		//touchListener = new TouchListener();
        Global.board.setOnTouchListener(touchListener);
        setContentView(Global.board);
        
        game = new GameObject();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Check if settings were changed
    	if(Integer.decode(prefs.getString("aiPref", "1")) != Global.difficulty || Integer.decode(prefs.getString("gameSizePref", "7")) != Global.gridSize || Integer.decode(prefs.getString("gameModePref", "0")) != (int) Global.gameType){
    		initializeNewGame();
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
        	Intent settingsActivity = new Intent(getBaseContext(),Preferences.class);
        	startActivity(settingsActivity);
            return true;
        case R.id.undo:
        	if(game!=null)
        		game.stop();
        	BoardTools.clearBoard();
        	BoardTools.undo();
        	game= new GameObject();
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