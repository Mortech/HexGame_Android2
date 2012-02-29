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

public class HexGame extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(BoardTools.teamGrid()==null){
        	initializeNewGame();//Must be set up immediately
        }
        else{
        	Global.setBoard(new BoardView(this));
        	//Add the touch listener
    		OnTouchListener touchListener = new OnTouchListener() {
    			
    			public boolean onTouch(View v, MotionEvent event) {
    				//Check if its a human's turn
    				if(Global.getCurrentPlayer()==1){
    					if(Global.getGameType()<2) 
    						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
    				}
    				else{
    					if((Global.getGameType()+1)%2>0) 
    						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
    				}
    				
    				return false;
    			}
            };
            Global.getBoard().setOnTouchListener(touchListener);
            setContentView(Global.getBoard());
        }
    }
    
    public boolean makeMove(int X, int Y, byte team){
    	for(int i=0;i<Global.getN();i++){
    		for(int j=0;j<Global.getN();j++){
    			if(BoardTools.getPolyXY()[i][j].getX()+2*Global.getHexLength()>X && X>BoardTools.getPolyXY()[i][j].getX() && Y>BoardTools.getPolyXY()[i][j].getY() && BoardTools.getPolyXY()[i][j].getY()+2*Global.getHexLength()>Y){
    				if(BoardTools.teamGrid()[i][j]==0){
    					Global.setPendingMove(new Posn(i,j));
    					return true;
    				}
    				else{
    					return false;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public void initializeNewGame(){
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Create our board
    	BoardTools.setGame(Integer.decode(prefs.getString("gameSizePref", "7")));
    	BoardTools.clearBoard();
    	Global.setBoard(new BoardView(this));
    	
    	//Make sure the board is empty and defaults are set
    	BoardTools.clearMoveList();
    	Global.setCurrentPlayer((byte) 1);
    	Global.setRunning(true);
    	Global.setPendingMove(null);
    	
    	//Set game mode
    	Global.setGameType(prefs.getString("gameModePref", "0"));
    	if(Global.getGameType()<2) Global.setPlayer1(new PlayerObject((byte)1));
		else Global.setPlayer1(new GameAI((byte)1,(byte)1));//Sets Player vs Ai
		if((Global.getGameType()+1)%2>0) Global.setPlayer2(new PlayerObject((byte)2));
		else Global.setPlayer2(new GameAI((byte)2,(byte)1));//Sets Player vs Ai
		
		//Create the game object
		@SuppressWarnings("unused")
		GameObject game = new GameObject();
		
		//Add the touch listener
		OnTouchListener touchListener = new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				//Check if its a human's turn
				if(Global.getCurrentPlayer()==1){
					if(Global.getGameType()<2) 
						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
				}
				else{
					if((Global.getGameType()+1)%2>0) 
						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
				}
				
				return false;
			}
        };
        Global.getBoard().setOnTouchListener(touchListener);
        setContentView(Global.getBoard());
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Check if settings were changed
    	if(Integer.decode(prefs.getString("gameSizePref", "7")) != Global.getN() || Integer.decode(prefs.getString("gameModePref", "0")) != (int) Global.getGameType()){
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
        	if(!Global.getMoveList().isEmpty()){
    			Posn lastMove = Global.getMoveList().get(Global.getMoveList().size()-1);
    			Global.setGameboard(lastMove.getX(), lastMove.getY(), (byte) 0);
    			Global.removeFromMoveList(lastMove);
        	}
        	BoardTools.updateCurrentPlayer();
    		Global.setRunning(true);
    		Global.setPendingMove(null);
    		for(int i=0;i<Global.getN();i++){
				for(int j=0;j<Global.getN();j++){
					if(Global.getGameboard()[i][j]==(byte) 3){
						Global.setGameboard(i, j, (byte) 1);
					}
					else if(Global.getGameboard()[i][j]==(byte) 4){
						Global.setGameboard(i, j, (byte) 2);
					}
				}
			}
        	//Create our board
        	Global.setBoard(new BoardView(this));
    		
    		//Create the game object
    		@SuppressWarnings("unused")
    		GameObject game = new GameObject();
    		
    		//Add the touch listener
    		OnTouchListener touchListener = new OnTouchListener() {
    			
    			public boolean onTouch(View v, MotionEvent event) {
    				//Check if its a human's turn
    				if(Global.getCurrentPlayer()==1){
    					if(Global.getGameType()<2) 
    						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
    				}
    				else{
    					if((Global.getGameType()+1)%2>0) 
    						makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
    				}
    				
    				return false;
    			}
            };
            Global.getBoard().setOnTouchListener(touchListener);
            setContentView(Global.getBoard());
        	Global.getBoard().invalidate();
            return true;
        case R.id.newgame:
        	initializeNewGame();
        	Global.getBoard().invalidate();
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