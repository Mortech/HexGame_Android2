package com.sam.hex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HexGame extends Activity {
    /** Called when the activity is first created. */
	int n=7;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(BoardTools.teamGrid()==null){
        	BoardTools.setGame(n);//Must be set up immediately
        }
        
        Global.setBoard(new BoardView(this));
        OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer());
				return false;
			}
        };
        Global.getBoard().setOnTouchListener(touchListener);
        setContentView(Global.getBoard());
        
//        PlayingEntity player1;
//		PlayingEntity player2;
//		
//		if(Global.getGameType()<2) player1=new PlayerObject((byte)1);
//		else player1=new GameAI((byte)1,(byte)1);// sets player vs Ai
//		
//		if((Global.getGameType()+1)%2>0) player2=new PlayerObject((byte)2);
//		else player2=new GameAI((byte)2,(byte)1);// sets player vs Ai
    }
    
    public boolean makeMove(int X, int Y, byte team){
    	for(int i=BoardTools.getN()-1;i>-1;i--){
    		for(int j=BoardTools.getN()-1;j>-1;j--){
    			if(X>BoardTools.getPolyXY()[i][j].getX() && Y>BoardTools.getPolyXY()[i][j].getY()){
    				if(BoardTools.teamGrid()[i][j]==0){
    					BoardTools.makeMove(i, j, team);
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
        	BoardTools.undo();
        	Global.getBoard().invalidate();
            return true;
        case R.id.newgame:
        	BoardTools.clearBoard();
        	BoardTools.clearMoveList();
        	Global.setCurrentPlayer((byte) 1);
        	Global.getBoard().invalidate();
            return true;
        case R.id.quit:
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    @Override
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