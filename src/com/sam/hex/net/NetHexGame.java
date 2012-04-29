package com.sam.hex.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sam.hex.GameAction;
import com.sam.hex.GameObject;
import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.Preferences;
import com.sam.hex.R;
import com.sam.hex.BoardView;
import com.sam.hex.Timer;
import com.sam.hex.net.NetGlobal;
import com.sam.hex.replay.FileExplore;
import com.sam.hex.replay.Replay;
import com.sam.hex.replay.Save;

public class NetHexGame extends Activity {
	public static boolean startNewGame = true;
	public static boolean replay = false;
	public static boolean replayRunning = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(NetHexGame.startNewGame){
        	initializeNewGame();//Must be set up immediately
        }
        else{
        	applyBoard();
        }
    }
    
    public void applyBoard(){
    	Global.viewLocation = NetGlobal.gameLocation;
    	setContentView(R.layout.game);
    	NetGlobal.game.board=(BoardView) findViewById(R.id.board);
    	NetGlobal.game.board.setOnTouchListener(new HexGame.TouchListener(NetGlobal.game));
    	
    	Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        Button undo = (Button) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	undo();
            }
        });
        
        Button newgame = (Button) findViewById(R.id.newgame);
        newgame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int which) {
            	        switch (which){
            	        case DialogInterface.BUTTON_POSITIVE:
            	            //Yes button clicked
            	        	initializeNewGame();
            	            break;
            	        case DialogInterface.BUTTON_NEGATIVE:
            	            //No button clicked
            	        	//Do nothing
            	            break;
            	        }
            	    }
            	};

            	AlertDialog.Builder builder = new AlertDialog.Builder(NetHexGame.this);
            	builder.setMessage(NetHexGame.this.getString(R.string.confirmNewgame)).setPositiveButton(NetHexGame.this.getString(R.string.yes), dialogClickListener).setNegativeButton(NetHexGame.this.getString(R.string.no), dialogClickListener).show();
            }
        });
        
        Button quit = (Button) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	quit();
            }
        });
        
        NetGlobal.game.player1Icon = (ImageButton) this.findViewById(R.id.p1);
        NetGlobal.game.player2Icon = (ImageButton) this.findViewById(R.id.p2);
        
        NetGlobal.game.timerText = (TextView) this.findViewById(R.id.timer);
        if(NetGlobal.game.timer.type==0 || NetGlobal.game.gameOver){
        	NetGlobal.game.timerText.setVisibility(View.GONE);
        } 
        NetGlobal.game.winnerText = (TextView) this.findViewById(R.id.winner);
        if(NetGlobal.game.gameOver) NetGlobal.game.winnerText.setText(NetGlobal.game.winnerMsg);
        NetGlobal.game.handler = new Handler();

        NetGlobal.game.replayForward = (ImageButton) this.findViewById(R.id.replayForward);
        NetGlobal.game.replayPlayPause = (ImageButton) this.findViewById(R.id.replayPlayPause);
        NetGlobal.game.replayBack = (ImageButton) this.findViewById(R.id.replayBack);
        NetGlobal.game.replayButtons = (RelativeLayout) this.findViewById(R.id.replayButtons);
    }
    
    private void initializeNewGame(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	startNewGame = false;
    	replayRunning = false;
    	
    	//Stop the old game
    	HexGame.stopGame(NetGlobal.game);
    	
    	//Create a new game object
    	NetGlobal.game = new GameObject(HexGame.setGrid(prefs, NetGlobal.gameLocation), true); 
    	
    	//Set players
    	HexGame.setType(prefs, NetGlobal.gameLocation, NetGlobal.game);
    	HexGame.setPlayer1(NetGlobal.game, new Runnable(){
			public void run(){
				initializeNewGame();
			}
		});
    	HexGame.setPlayer2(NetGlobal.game, new Runnable(){
			public void run(){
				initializeNewGame();
			}
		});
    	HexGame.setNames(prefs, NetGlobal.gameLocation, NetGlobal.game);
    	HexGame.setColors(prefs, NetGlobal.gameLocation, NetGlobal.game);
	    NetGlobal.game.timer = new Timer(NetGlobal.game, 0,0);
    	
	    //Display board
	    applyBoard();
    	
        //Start the game object
	    NetGlobal.game.start();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	//Check if settings were changed and we need to run a new game
    	 if(replayRunning){
     		//Do nothing
     	}
    	 else if(replay){
    		replay = false;
    		replay(800);
    	}
    	else if(startNewGame || HexGame.somethingChanged(prefs, NetGlobal.gameLocation, NetGlobal.game)){
    		initializeNewGame();
    	}
    	else{//Apply minor changes without stopping the current game
    		HexGame.setColors(prefs, NetGlobal.gameLocation, NetGlobal.game);
    		HexGame.setNames(prefs, NetGlobal.gameLocation, NetGlobal.game);
    		NetGlobal.game.moveList.replay(0,NetGlobal.game);
    		GameAction.checkedFlagReset(NetGlobal.game);
    		GameAction.checkWinPlayer(1,NetGlobal.game);
    		GameAction.checkWinPlayer(2,NetGlobal.game);
    		GameAction.checkedFlagReset(NetGlobal.game);
	    	
	    	//Apply everything
	    	NetGlobal.game.board.invalidate();
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
            return true;
        case R.id.saveReplay:
        	Save save = new Save(NetGlobal.game);
        	save.showSavingDialog();
        	return true;
        case R.id.quit:
        	quit();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void undo(){
    	GameAction.undo(NetGlobal.gameLocation, NetGlobal.game);
    }
    
    private void newGame(){
    	if(NetGlobal.game.player1.supportsNewgame() && NetGlobal.game.player2.supportsNewgame()){
			if(replayRunning){
				replayRunning = false;
				try {
					replayThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			initializeNewGame();
			NetGlobal.game.board.invalidate();
    	}
    }
    
    private Thread replayThread;
    private void replay(int time){
    	//Create our board
    	NetGlobal.game.clearBoard();
        
    	if(NetGlobal.game.moveNumber>1) NetGlobal.game.currentPlayer=(NetGlobal.game.currentPlayer%2)+1;
	    
    	replayRunning = true;
    	replayThread = new Thread(new Replay(time, new Handler(), new Runnable(){
			public void run(){
				NetGlobal.game.timerText.setVisibility(View.GONE);
				NetGlobal.game.winnerText.setVisibility(View.GONE);
//				Global.replayButtons.setVisibility(View.VISIBLE);
			}
		}, new Runnable(){
			public void run(){
				if(NetGlobal.game.timer.type!=0) NetGlobal.game.timerText.setVisibility(View.VISIBLE);
//				Global.replayButtons.setVisibility(View.GONE);
			}
		},NetGlobal.game), "replay");
		replayThread.start();
    }
    
    private void quit(){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	HexGame.stopGame(NetGlobal.game);
    	        	finish();
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	        	//Do nothing
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(this.getString(R.string.confirmExit)).setPositiveButton(this.getString(R.string.yes), dialogClickListener).setNegativeButton(this.getString(R.string.no), dialogClickListener).show();
    }
}