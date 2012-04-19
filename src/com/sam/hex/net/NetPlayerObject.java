package com.sam.hex.net;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

import android.graphics.Point;

public class NetPlayerObject implements PlayingEntity {
	int team;
	MoveListener listener;
	
	public NetPlayerObject(int i) {
		this.team=i;//Set the player's team
		this.listener = new MoveListener();
	}
	
	public void getPlayerTurn() {
		if(Global.moveNumber>1){
			//TODO Send our move
		}
		
		NetGlobal.hex = null;
		looper: while (true) {
			Point hex = NetGlobal.hex;
			while (hex == null) {
				hex = NetGlobal.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Global.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				NetGlobal.hex = null;
				break;
			}
			else if (GameAction.makeMove(this, (byte) team, hex)) {
				NetGlobal.hex = null;
				break;
			}
			NetGlobal.hex = null;
		}
	}
	
	public void undoCalled(){
	}

	public void newgameCalled() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public boolean supportsUndo() {
		//TODO Ask to undo
		return false;
	}

	@Override
	public boolean supportsNewgame() {
		//TODO Ask for a new game
		return false;
	}

	@Override
	public void colorChanged() {
	}

	@Override
	public void nameChanged() {
	}

	@Override
	public void quit() {
		listener.stop();
	}

	@Override
	public void win() {
	}

	@Override
	public void lose() {
		//TODO Send move
	}

	@Override
	public boolean supportsSave() {
		return false;
	}
}