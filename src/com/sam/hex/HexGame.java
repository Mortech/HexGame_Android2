package com.sam.hex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Point;

import com.sam.hex.ai.bee.BeeGameAI;
import com.sam.hex.ai.will.GameAI;
import com.sam.hex.lan.LANGlobal;
import com.sam.hex.lan.LocalLobbyActivity;
import com.sam.hex.lan.LocalPlayerObject;
import com.sam.hex.net.NetGlobal;
import com.sam.hex.net.NetLobbyActivity;
import com.sam.hex.net.NetPlayerObject;
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
        	applyBoard();
        }
    }
    
    public void applyBoard(){
    	setContentView(R.layout.game);
    	Global.board=(BoardView) findViewById(R.id.board);
    	Global.board.setOnTouchListener(new TouchListener());
    	
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

            	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(HexGame.this, android.R.style.Theme_Light));
            	builder.setMessage(HexGame.this.getString(R.string.confirmNewgame)).setPositiveButton(HexGame.this.getString(R.string.yes), dialogClickListener).setNegativeButton(HexGame.this.getString(R.string.no), dialogClickListener).show();
            }
        });
        
        Button settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),Preferences.class));
            }
        });
        
        Button quit = (Button) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	quit();
            }
        });
        
        Global.player1Icon = (ImageButton) this.findViewById(R.id.p1);
        Global.player2Icon = (ImageButton) this.findViewById(R.id.p2);
        
        Global.timerText = (TextView) this.findViewById(R.id.timer);
        if(Global.game.timer.type==0 || Global.game.gameOver){
        	Global.timerText.setVisibility(View.GONE);
        } 
        Global.winnerText = (TextView) this.findViewById(R.id.winner);
        if(Global.game.gameOver) Global.winnerText.setText(Global.winnerMsg);
        GameAction.handler = new Handler();

        Global.replayForward = (ImageButton) this.findViewById(R.id.replayForward);
        Global.replayPlayPause = (ImageButton) this.findViewById(R.id.replayPlayPause);
        Global.replayBack = (ImageButton) this.findViewById(R.id.replayBack);
        Global.replayButtons = (RelativeLayout) this.findViewById(R.id.replayButtons);
    }
    
    class TouchListener implements OnTouchListener{
    	public boolean onTouch(View v, MotionEvent event){
    		int eventaction = event.getAction();
    		if(eventaction==MotionEvent.ACTION_UP){
    			int x = (int)event.getX();
				int y = (int)event.getY();
				for(int xc = 0; xc < Global.game.gamePiece.length; xc++){
					for(int yc=0; yc<Global.game.gamePiece[0].length; yc++)
						if(Global.game.gamePiece[xc][yc].contains(x, y)){
							if(Global.game!=null)GameAction.setPiece(new Point(xc,yc));
							return false;
						}
				}
    		}
    		
			return true;
    	}
    }
    
    private void initializeNewGame(){
    	startNewGame = false;
    	
    	//Stop the old game
    	stopGame();
    	
    	//Create a new game object
    	Global.game = new GameObject(); 
    	
    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Set defaults
    	Global.gameLocation = Integer.parseInt(prefs.getString("gameLocation", "0"));
    	Global.game.swap = prefs.getBoolean("swapPref", true);
    	replayRunning=false;
    	
    	//Set players
    	setType(prefs);
    	setPlayer1();
    	setPlayer2();
    	setNames(prefs);
    	setColors(prefs);
	    Global.game.timer = new Timer(new Handler(), Global.game, Integer.parseInt(prefs.getString("timerPref", "0")),Integer.parseInt(prefs.getString("timerTypePref", "0")));
    	
    	//Create our board
    	setGrid(prefs);
    	Global.game.gamePiece=new RegularPolygonGameObject[Global.game.gridSize][Global.game.gridSize];
	    applyBoard();
    	
        //Create the game object
	    Global.game.start();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
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
    		Global.gameLocation = 1;
    		startNewGame = true;
        	startActivity(new Intent(getBaseContext(),LocalLobbyActivity.class));
        	finish();
    	}
    	else if(Integer.decode(prefs.getString("gameLocation", "0")) != Global.gameLocation && Integer.decode(prefs.getString("gameLocation", "0")) == 2){
    		//Go to the net lobby
    		Global.gameLocation = 2;
    		startNewGame = true;
        	startActivity(new Intent(getBaseContext(),NetLobbyActivity.class));
        	finish();
    	}
    	else if(HexGame.startNewGame || somethingChanged(prefs)){
    		initializeNewGame();
    	}
    	else{//Apply minor changes without stopping the current game
    		setColors(prefs);
    		setNames(prefs);
    		Global.game.moveList.replay(0);
    		GameAction.checkedFlagReset();
    		GameAction.checkWinPlayer(1);
    		GameAction.checkWinPlayer(2);
    		GameAction.checkedFlagReset();
	    	
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
            return true;
        case R.id.saveReplay:
        	Save save = new Save();
        	save.showSavingDialog();
        	return true;
        case R.id.quit:
        	quit();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	//If the board's empty, just trigger "startNewGame"
    	if(Global.game.moveNumber==1 && Global.gameLocation==0 && Global.game.timer.type==0) HexGame.startNewGame=true;
    }
    
    public static void stopGame(){
    	if(Global.game!=null){
    		Global.game.stop();
    		try {
				Global.game.gameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
    			Global.game.player1.setName(LANGlobal.localPlayer.playerName);
        		Global.game.player2.setName(prefs.getString("lanPlayerName", "Player"));
    		}
    		else{
    			Global.game.player1.setName(prefs.getString("lanPlayerName", "Player"));
        		Global.game.player2.setName(LANGlobal.localPlayer.playerName);
    		}
    	}
    	else if(Global.gameLocation==2){
    		//Playing over the net
    		for(int i=0;i<NetGlobal.members.size();i++){
    			if(NetGlobal.members.get(i).place==1){
    				Global.game.player1.setName(NetGlobal.members.get(i).name);
    			}
    			else if(NetGlobal.members.get(i).place==2){
    				Global.game.player2.setName(NetGlobal.members.get(i).name);
    			}
    		}
    	}
    	else{
    		//Playing on the same phone
    		Global.game.player1.setName(prefs.getString("player1Name", "Player1"));
    		Global.game.player2.setName(prefs.getString("player2Name", "Player2"));
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
    			Global.game.player1.setColor(LANGlobal.localPlayer.playerColor);
        		Global.game.player2.setColor(prefs.getInt("lanPlayerColor", Global.player1DefaultColor));
    		}
    		else{
    			Global.game.player1.setColor(prefs.getInt("lanPlayerColor", Global.player1DefaultColor));
        		Global.game.player2.setColor(LANGlobal.localPlayer.playerColor);
    		}
    	}
    	else if(Global.gameLocation==2){
    		//Playing on the net
    		Global.game.player1.setColor(Global.player1DefaultColor);
    		Global.game.player2.setColor(Global.player2DefaultColor);
    	}
    	else{
    		//Playing on the same phone
    		Global.game.player1.setColor(prefs.getInt("player1Color", Global.player1DefaultColor));
    		Global.game.player2.setColor(prefs.getInt("player2Color", Global.player2DefaultColor));
    	}
    }
    
    private void setGrid(SharedPreferences prefs){
    	if(Global.gameLocation==0){
    		//Playing on the same phone
    		Global.game.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    		if(Global.game.gridSize==0) Global.game.gridSize=Integer.decode(prefs.getString("customGameSizePref", "7"));
    	}
    	else if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			Global.game.gridSize=LANGlobal.localPlayer.gridSize;
    		}
    		else{
    			Global.game.gridSize=Integer.decode(prefs.getString("gameSizePref", "7"));
    		}
    	}
    	else if(Global.gameLocation==2){
    		//Playing over the net
    		Global.game.gridSize = NetGlobal.gridSize;
    	}
    	
    	//We don't want 0x0 games
    	if(Global.game.gridSize<=0) Global.game.gridSize=1;
    }
    
    private void setType(SharedPreferences prefs){
    	if(Global.gameLocation==0){
    		Global.player1Type=(byte)Integer.parseInt(prefs.getString("player1Type", "0"));
        	Global.player2Type=(byte)Integer.parseInt(prefs.getString("player2Type", "0"));
    	}
    	else if(Global.gameLocation==1){
    		//Playing over LAN
    		if(LANGlobal.localPlayer.firstMove){
    			Global.player1Type=(byte)2;
    			Global.player2Type=(byte)Integer.parseInt(prefs.getString("lanPlayerType", "0"));
    		}
    		else{
    			Global.player1Type=(byte)Integer.parseInt(prefs.getString("lanPlayerType", "0"));
    			Global.player2Type=(byte)2;
    		}
    	}
    	else if(Global.gameLocation==2){
    		//Playing over the net
    		for(int i=0;i<NetGlobal.members.size();i++){
    			if(NetGlobal.members.get(i).place==1){
    				if(prefs.getString("netUsername", "").toLowerCase().equals(NetGlobal.members.get(i).name.toLowerCase())){
    					Global.player1Type=(byte)0;
    				}
    				else{
    					Global.player1Type=(byte)3;
    				}
    			}
    			else if(NetGlobal.members.get(i).place==2){
    				if(prefs.getString("netUsername", "").toLowerCase().equals(NetGlobal.members.get(i).name.toLowerCase())){
    					Global.player2Type=(byte)0;
    				}
    				else{
    					Global.player2Type=(byte)3;
    				}
    			}
    		}
    	}
    }
    
    private void setPlayer1(){
    	if(Global.player1Type==(byte) 0) Global.game.player1=new PlayerObject((byte)1);
		else if(Global.player1Type==(byte) 1) Global.game.player1=new GameAI((byte)1);
		else if(Global.player1Type==(byte) 2) Global.game.player1=new LocalPlayerObject((byte)1);
		else if(Global.player1Type==(byte) 3) Global.game.player1=new NetPlayerObject((byte)1, new Handler(), new Runnable(){
			public void run(){
				initializeNewGame();
			}
		});
		else if(Global.player1Type==(byte) 4) Global.game.player1=new BeeGameAI(1);
    }
    
    private void setPlayer2(){
		if(Global.player2Type==(byte) 0) Global.game.player2=new PlayerObject((byte)2);
		else if(Global.player2Type==(byte) 1) Global.game.player2=new GameAI((byte)2);
		else if(Global.player2Type==(byte) 2) Global.game.player2=new LocalPlayerObject((byte)2);
		else if(Global.player2Type==(byte) 3) Global.game.player2=new NetPlayerObject((byte)2, new Handler(), new Runnable(){
			public void run(){
				initializeNewGame();
			}
		});
		else if(Global.player2Type==(byte) 4) Global.game.player2=new BeeGameAI(2);
    }
    
    private void undo(){
    	if(Global.game.player1.supportsUndo() && Global.game.player2.supportsUndo())
	    	GameAction.undo();
    }
    
    private void newGame(){
    	if(Global.game.player1.supportsNewgame() && Global.game.player2.supportsNewgame()){
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
    	if(Global.game==null) return true;
    	if(Global.gameLocation==0){
    		return Integer.decode(prefs.getString("gameLocation", "0")) != Global.gameLocation
    				|| (Integer.decode(prefs.getString("gameSizePref", "7")) != Global.game.gridSize && Integer.decode(prefs.getString("gameSizePref", "7")) != 0) 
    				|| (Integer.decode(prefs.getString("customGameSizePref", "7")) != Global.game.gridSize && Integer.decode(prefs.getString("gameSizePref", "7")) == 0)
    				|| Integer.decode(prefs.getString("player1Type", "0")) != (int) Global.player1Type 
    	    		|| Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type 
    	    	    || Integer.decode(prefs.getString("timerTypePref", "0")) != Global.game.timer.type;
    	}
    	if(Global.gameLocation==1){
    		return Integer.decode(prefs.getString("gameLocation", "0")) != Global.gameLocation
        			|| !(Integer.decode(prefs.getString("lanPlayerType", "0")) == (int) Global.player1Type || Integer.decode(prefs.getString("lanPlayerType", "0")) == (int) Global.player2Type);
    	}
    	else if(Global.gameLocation==2){
    		return Integer.decode(prefs.getString("gameLocation", "0")) != Global.gameLocation;
    	}
    	else{
    		return true;
    	}
    }
    
    private Thread replayThread;
    private void replay(int time){
    	//Create our board
    	Global.game.gamePiece=new RegularPolygonGameObject[Global.game.gridSize][Global.game.gridSize];
    	applyBoard();
        
    	if(Global.game.moveNumber>1) Global.game.currentPlayer=(Global.game.currentPlayer%2)+1;
	    
    	replayRunning = true;
		replayThread = new Thread(new Replay(time, new Handler()), "replay");
		replayThread.start();
    }
    
    private void quit(){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	stopGame();
    	        	android.os.Process.killProcess(android.os.Process.myPid());
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	        	//Do nothing
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Light));
    	builder.setMessage(this.getString(R.string.confirmExit)).setPositiveButton(this.getString(R.string.yes), dialogClickListener).setNegativeButton(this.getString(R.string.no), dialogClickListener).show();
    }
}