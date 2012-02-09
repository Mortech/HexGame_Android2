package com.sam.hex;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HexGame extends Activity {
    /** Called when the activity is first created. */
	int n=5;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        BoardTools.setGame(n);//Must be set up immediately
        
        final BoardView board = new BoardView(this);//Relies on the game being set up to draw the correct board
        OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(makeMove((int)event.getX(), (int)event.getY(), Global.getCurrentPlayer())){
		        	Global.setCurrentPlayer();
		        	board.invalidate();//Redraws board
		        }
				return false;
			}
        };
        board.setOnTouchListener(touchListener);
        setContentView(board);
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
}