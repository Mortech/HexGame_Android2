package com.sam.hex;

import java.io.Serializable;

public class MoveList implements Serializable {
	private static final long serialVersionUID = 1L;
	public Move thisMove;
	public MoveList nextMove;
	public MoveList(){
		
	}
	
	public MoveList(int x, int y, byte teamNumber, long time){
		thisMove= new Move(x,y,teamNumber, time, Global.moveNumber);
	}
	
	public MoveList(MoveList oldMove, int x, int y, byte teamNumber, long time){
		thisMove= new Move(x,y,teamNumber, time, Global.moveNumber);
		nextMove=oldMove;
	}
	public MoveList(MoveList oldMove, Move thisMove){
		this.thisMove= thisMove;
		nextMove=oldMove;
	}
	public Move getmove(){
		return thisMove;
	}
	/* do not use makeMove might not work with
	 * base cases and is not tested*/
	public void makeMove(int x, int y, byte teamNumber, long time){
		nextMove=new MoveList(nextMove, thisMove);
		thisMove= new Move(x, y, teamNumber, time, Global.moveNumber);
	}
	public void undo(){
		if (thisMove==null) return; 
		Global.gamePiece[thisMove.getX()][thisMove.getY()].setTeam((byte) 0);
		thisMove=nextMove.thisMove;
		nextMove=nextMove.nextMove;
		
	}
	public void undoTwo(){
		if (thisMove==null)return;
		nextMove.undo();
		undo();
	}
	//for replays
	public void replay(int time){
		if (thisMove==null) return;
		if (nextMove!=null) nextMove.replay(time);
		try {
			if(HexGame.replayRunning) Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Global.gamePiece[thisMove.getX()][thisMove.getY()].setTeam(thisMove.getTeam());
		Global.board.postInvalidate();
	}	
}