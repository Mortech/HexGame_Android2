/**
 * 
 */
package com.sam.hex;

/**
 * @author Sam Laane
 *
 */
public interface PlayingEntity {
	/**
	 * Must call GameAction.makeMove() eventually
	 * 
	 * Logic for making a move is here.
	 * */
	public void getPlayerTurn();
	
	/**
	 * Undo has been applied. The last move is blank again.
	 * */
	public void undoCalled();
	
	/**
	 * New game has been applied. The entire game board is wiped. 
	 * Do not call GameAction.makeMove() and quit as soon as possible.
	 * */
	public void newgameCalled();
	
	/**
	 * Will you allow an undo?
	 * Return true if your PlayingEntity supports undo
	 * Return false if it doesn't or if you want an asynchronous undo
	 * (such as in LAN or Net play)
	 * */
	public boolean supportsUndo();
	
	/**
	 * Will you allow a new game?
	 * Return true if your PlayingEntity supports new games
	 * Return false if it doesn't or if you want an asynchronous new game
	 * (such as in LAN or Net play)
	 * */
	public boolean supportsNewgame();
	
	/**
	 * The player's color has been changed!
	 * Locally, all the pieces have been swapped to the new color.
	 * This is only useful for LAN play.
	 * */
	public void colorChanged();
	
	/**
	 * The player's name has been changed!
	 * Locally, the new name has been switched to.
	 * This is only useful for LAN play.
	 * */
	public void nameChanged();
	
	/**
	 * The game is over. Die gracefully.
	 * */
	public void quit();
}
