package com.sam.hex;

import android.graphics.Point;

public class PlayerObject implements PlayingEntity {
	private String name;
	private int color;
	private long timeLeft;
	private byte team;
	private Point hex;
	
	public PlayerObject(byte i) {
		this.team=i;//Set the player's team
	}
	
	@Override
	public void getPlayerTurn() {
		hex = null;
		while (true) {
			while (hex == null) {
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (hex.equals(new Point(-1,-1))){
				hex = null;
				break;
			}
			if (GameAction.makeMove(this, team, hex)) {
				hex = null;
				break;
			}
			hex = null;
		}
	}
	
	@Override
	public void undoCalled(){
		hex = new Point(-1,-1);
	}
	
	@Override
	public void newgameCalled() {
		hex = new Point(-1,-1);
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
	public void quit() {
		hex = new Point(-1,-1);
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

	@Override
	public void endMove() {
		hex = new Point(-1,-1);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public void setTime(long time) {
		this.timeLeft = time;
	}

	@Override
	public long getTime() {
		return timeLeft;
	}

	@Override
	public void setMove(Point hex) {
		this.hex = hex;
	}
}