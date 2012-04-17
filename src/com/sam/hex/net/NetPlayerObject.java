package com.sam.hex.net;

import com.sam.hex.GameAction;
import com.sam.hex.Global;
import com.sam.hex.PlayingEntity;

import android.graphics.Point;

public class NetPlayerObject implements PlayingEntity {
	int team;
	
	public NetPlayerObject(int i) {
		this.team=i;//Set the player's team
	}
	
	public void getPlayerTurn() {
		GameAction.hex = null;
		looper: while (true) {
			Point hex = GameAction.hex;
			while (hex == null) {
				hex = GameAction.hex;
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Global.gameOver) break looper;
			}
			if (hex.equals(new Point(-1,-1))){
				GameAction.hex = null;
				break;
			}
			if (Global.gamePiece[hex.x][hex.y].getTeam() == 0) {
				GameAction.makeMove(this, (byte) team, hex);
				GameAction.hex = null;
				break;
			}
			GameAction.hex = null;
		}
	}
	
	public void undoCalled(){
	}

	public void newgameCalled() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public boolean supportsUndo() {
		return true;
	}

	@Override
	public boolean supportsNewgame() {
		return true;
	}

	@Override
	public void colorChanged() {
	}

	@Override
	public void nameChanged() {
	}

	@Override
	public void quit() {
		GameAction.hex = new Point(-1,-1);
	}

	@Override
	public void win() {
	}

	@Override
	public void lose() {
	}

	@Override
	public boolean supportsSave() {
		return false;
	}
}