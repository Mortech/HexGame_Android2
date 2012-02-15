package com.sam.hex;

import android.graphics.Color
;



import sl.shapes.RegularPolygon;


public class RegularPolygonGameObject  {

	RegularPolygon Hex;
	private byte teamNumber; // 1 is left-right, 2 is top-down
	private int objectColor = Color.WHITE;
	boolean checkedflage = false;

	public RegularPolygonGameObject(double x, double y, double r,
			int vertexCount) {
		Hex = new RegularPolygon(x, y, r, vertexCount);

	}

	public RegularPolygonGameObject(double x, double y, double r,
			int vertexCount, double startAngle) {
		Hex = new RegularPolygon(x, y, r, vertexCount, startAngle);
	}

	public void update(double x, double y, double r, int vertexCount,
			double startAngle) {
		Hex = new RegularPolygon(x, y, r, vertexCount, startAngle);
	}

	public void setTeam(byte t) {
		teamNumber = t;
		if (teamNumber == 1)
			setColor(Global.playerOne);
		else
			setColor(Global.playerTwo);
	}

	public byte getTeam() {
		return teamNumber;
	}

	public boolean checkpiece(byte team, int x, int y,
			RegularPolygonGameObject[][] gamePeace) {
		if (team == teamNumber && !checkedflage) {
			checkedflage = !checkedflage;
			if (checkSpot(team, x, y) || checkWinTeam(team, x, y, gamePeace)) {
				objectColor = Color.GREEN;
				return true;
			}
		}
		return false;

	}

	public static boolean checkWinTeam(byte team, int x, int y,
			RegularPolygonGameObject[][] gamePeace) {
		if (y < gamePeace.length && x - 1 >= 0
				&& gamePeace[x - 1][y].checkpiece(team, x - 1, y, gamePeace)) {
			return true;
		}
		if (y < gamePeace.length && x + 1 < gamePeace.length
				&& gamePeace[x + 1][y].checkpiece(team, x + 1, y, gamePeace)) {
			return true;
		}
		if (x < gamePeace.length && y - 1 >= 0
				&& gamePeace[x][y - 1].checkpiece(team, x, y - 1, gamePeace)) {
			return true;
		}
		if (x < gamePeace.length && y + 1 < gamePeace.length
				&& gamePeace[x][y + 1].checkpiece(team, x, y + 1, gamePeace)) {
			return true;
		}
		if (y + 1 < gamePeace.length
				&& x - 1 >= 0
				&& gamePeace[x - 1][y + 1].checkpiece(team, x - 1, y + 1,
						gamePeace)) {
			return true;
		}
		if (y - 1 < gamePeace.length
				&& x + 1 < gamePeace.length
				&& y - 1 >= 0
				&& gamePeace[x + 1][y - 1].checkpiece(team, x + 1, y - 1,
						gamePeace)) {
			return true;
		}


		return false;
	}

	public static boolean checkSpot(byte team, int x, int y) {
		if (team == 1 && x == 0) {
			return true;
		}
		if (team == 2 && y == 0) {
			return true;
		}
		return false;
	}

	public void setColor(int c) {
		objectColor = c;
	}

	public int getColor() {
		return objectColor;
	}



	public boolean contains(double x, double y) {

		return Hex.contains((int)x,(int) y);
	}


}
