/**
 * 
 */
package com.sam.hex;

import android.graphics.Point;

/**
 * @author Sam Laane
 *
 */
public interface PlayingEntity {
	public void getPlayerTurn();
	public void undo(Point hex);
}
