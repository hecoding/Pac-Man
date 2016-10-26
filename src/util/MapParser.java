package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.Map;
import model.Map.CellType;

public class MapParser {
	
	/** Parse a file get from "fname" and builds the map.
	 * The beginning must be indicated with an "@" character,
	 * every food bit with "#" and the empty cell with "0".
	 * 
	 * @param fname Path of the text file
	 * @param maxFood Number of bits of food
	 * @return Built map object
	 * @throws IOException If the file hasn't been found
	 */
	public static Map parse(String fname, int maxFood) throws IOException {
		ArrayList<ArrayList<CellType>> cells = new ArrayList<ArrayList<CellType>>();
		BufferedReader br = new BufferedReader(new FileReader(fname));
		String thisLine;
		
        while ((thisLine = br.readLine()) != null) {
        	String[] columns = thisLine.split(" ");
        	ArrayList<CellType> row = new ArrayList<>(columns.length);
        	
           for (String cell : columns) {
        	   if(cell.equals("@"))
        		   row.add(CellType.beginning);
        	   else if (cell.equals("#"))
        		   row.add(CellType.food);
        	   else
        		   row.add(CellType.nothing);
           }
           cells.add(row);
        }
		
		br.close();
		
		CellType[][] coolcells = new CellType[cells.size()][cells.get(0).size()];
		
		for (int i = 0; i < cells.size(); i++) {
			for (int j = 0; j < cells.get(0).size(); j++) {
				coolcells[i][j] = cells.get(i).get(j);
			}
		}
		
		return new Map(coolcells, maxFood);
	}
	
}
