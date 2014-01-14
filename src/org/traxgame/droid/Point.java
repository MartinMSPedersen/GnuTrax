package org.traxgame.droid;

import org.traxgame.main.Traxboard;

public class Point {
	private int x,y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	private String getRowColForPos(int x, int y) {
		StringBuilder sb = new StringBuilder();
		// System.out.println("POS: x: " + x + " Y: " + y);
		switch (x) {
		case 0:
			sb.append("@");
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			sb.append(Character.toString((char) (x - 1 + 65)));
			break;
		}
		sb.append(y);
		return sb.toString();
	}

	public String getPositionWithMove(int tileType) {
		StringBuilder sb = new StringBuilder();
		sb.append(getRowColForPos(x, y));
		switch (tileType) {
		case Traxboard.NS:
		case Traxboard.EW:
			sb.append("+");
			break;
		case Traxboard.SE:
		case Traxboard.WN:
			sb.append("/");
			break;
		case Traxboard.NE:
		case Traxboard.WS:
			sb.append("\\");
			break;
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}
	
}
