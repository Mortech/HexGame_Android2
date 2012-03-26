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
	public boolean undoCalled();
	public boolean newgameCalled();
}
