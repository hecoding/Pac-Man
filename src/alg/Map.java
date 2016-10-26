package model;

import model.Ant.Position;

public class Map implements Cloneable {
	private CellType[][] map;
	private int foodHere;
	
	public Map(CellType[][] cells, int food) {
		this.map = cells;
		this.foodHere = food;
	}
	
	public int getRows() {
		return this.map.length;
	}
	
	public int getColumns() {
		return this.map[0].length;
	}
	
	public int getFoodHere() {
		return this.foodHere;
	}
	
	public CellType get(int row, int column) {
		return this.map[row][column];
	}
	
	public CellType get(Position pos) {
		return get(pos.y, pos.x);
	}
	
	public void set(CellType cell, int row, int column) {
		this.map[row][column] = cell;
	}
	
	public void set(CellType cell, Position pos) {
		set(cell, pos.y, pos.x);
	}
	
	public Map clone() {
		return new Map(cloneArray(this.map), this.foodHere);
	}
	
	/**
	 * Clones the provided array
	 * 
	 * @param src
	 * @return a new clone of the provided array
	 */
	public static CellType[][] cloneArray(CellType[][] src) {
	    int length = src.length;
	    CellType[][] target = new CellType[length][src[0].length];
	    for (int i = 0; i < length; i++) {
	        System.arraycopy(src[i], 0, target[i], 0, src[i].length);
	    }
	    return target;
	}
	
	public static enum CellType {
		beginning, nothing, food, eatenfood, trail
	}
}
