/**
 * 
 */
package com.sam.hex;

/**
 * @author Sam Laane
 *
 */
public interface PlayingEntity {
	public void getPlayerTurn();
	public void undoCalled();
	public void newgameCalled();
	public boolean supportsUndo();
	public boolean supportsNewgame();
}
