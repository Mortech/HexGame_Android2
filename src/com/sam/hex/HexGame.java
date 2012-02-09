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
        
        final GameLogic game = new GameLogic();
        game.setGame(n);//Must be set up immediately
        
        final BoardView board = new BoardView(this);//Relies on the game being set up to draw the correct board
        OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        game.makeMove((int)event.getX(), (int)event.getY(), 1);
		        board.invalidate();
				return false;
			}
        };
        board.setOnTouchListener(touchListener);
        setContentView(board);
    }
}