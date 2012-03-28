package com.sam.hex;

import android.os.Looper;
import android.widget.Toast;

public class AnnounceWinner implements Runnable{
	int team;
	public AnnounceWinner(int team){
		this.team = team;
	}
	
	@Override
	public void run(){
		Looper.prepare();
		if(team==1){
			Toast.makeText(Global.board.getContext(), Global.player1Name+" wins!", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(Global.board.getContext(), Global.player2Name+" wins!", Toast.LENGTH_SHORT).show();
		}
		Looper.loop();
	}
}