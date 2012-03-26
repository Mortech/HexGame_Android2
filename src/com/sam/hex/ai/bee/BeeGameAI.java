package com.sam.hex.ai.bee;

import android.graphics.Point;

import com.sam.hex.GameAction;
import com.sam.hex.PlayingEntity;

public class BeeGameAI implements PlayingEntity {
	int team = 1;
	
	public BeeGameAI(int team){
		this.team = team;
		new Thread(new Bee(team), "bee").start();
	}

	@Override
	public void getPlayerTurn() {
		while(BeeMove.move==null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		GameAction.makeMove(this, (byte) team, BeeMove.move);
		BeeMove.move = null;
	}

	@Override
	public void undo(Point hex) {
		//Do nothing
	}
	
}