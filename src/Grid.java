import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

public class Grid {
	private Square[][] table;
	
	public final int[][] cornerCoords = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
	public final int[][] sideCoords = {{0, 1}, {1, 0}, {1, 2}, {2, 1}};
	
	private int x, y, size, inc;
	
	public Grid(int x, int y, int size) {
		table = new Square[3][3];
		this.x = x;
		this.y = y;
		this.size = size;
		inc = size/3;
		
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				table[i][j] = new Square(x+inc*j+2, y+inc*i+2, inc-4, i, j);
			}
		}
	}
	
	public int getSize() {return size;}
	
	public Square[] get1DTable() {
		Square[] tableArray = {
			table[0][0], table[0][1], table[0][2],
			table[1][0], table[1][1], table[1][2],
			table[2][0], table[2][1], table[2][2]
		};
		return tableArray;
	}
	
	public Square getSquare(int row, int col) {return table[row][col];}
	
	public void drawMe(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(x+inc-2, y-2, 4, size+4);//left vertical
		g.fillRect(x+inc*2-2, y-2, 4, size+4);//right vertical
		g.fillRect(x-2, y+inc-2, size+4, 4);//upper horizontal
		g.fillRect(x-2, y+inc*2-2, size+4, 4);//lower horizontal
		
		for(int i = 0; i < table.length; i++)
			for(int j = 0; j < table[i].length; j++)
				table[i][j].drawMe(g);
	}
	
	public boolean mark(Player player, int cx, int cy) {
		for(Square[] line: table) {
			for(Square box: line) {
				if(box.clickable(cx, cy)) {
					box.setPlayer(player);
					if(Runner.debug) System.out.println("GRID: set square " + box);
					return true;
				}
			}
		}
		
		if(Runner.debug) System.out.println("grid mark called on used box by player " + player.getName() + ": "+cx+","+cy);
		return false;
	}
	
	public Square[][] getTokenSets(Player player, int numVals) { return getTokenSets(player, numVals, false); }
	public Square[][] getTokenSets(Player player, int numVals, boolean othersEmpty) {
		//the idea is to return the squares that would make a tic-tac-toe if filled.
		//changed to just return all the sets of three in a row with two of the given value
		ArrayList<Square[]> matches = new ArrayList<Square[]>();
		
		Square[][] iTab = new Square[3][3];
		Square[] diag1 = new Square[3], diag2 = new Square[3];
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++)
				iTab[i][j] = table[j][i];
			
			if(hasRightNum(player, table[i], numVals, othersEmpty))
				matches.add(table[i]);
			if(hasRightNum(player, iTab[i], numVals, othersEmpty))
				matches.add(iTab[i]);
			
			diag1[i] = table[i][i];
			diag2[i] = table[2-i][i];
		}
		
		if(hasRightNum(player, diag1, numVals, othersEmpty))
			matches.add(diag1);
		if(hasRightNum(player, diag2, numVals, othersEmpty))
			matches.add(diag2);
		
		return matches.toArray(new Square[0][0]);
	}
	
	private boolean hasRightNum(Player player, Square[] line, int numVals) { return hasRightNum(player, line, numVals, false); }
	private boolean hasRightNum(Player player, Square[] line, int numVals, boolean othersEmpty) {
		int count = 0;
		for(Square box: line) {
			if(box.getPlayer() == player)
				count++;
			else if(othersEmpty && box.getPlayer() != null)
				return false; // returns false if a box is used by the other player, if we said we don't want that
		}
		
		return count == numVals;
	}
	
	//return boxes with value from entire grid
	public Square[] getBoxMatches(Player player) {
		ArrayList<Square> boxes = new ArrayList<Square>();
		
		for(Square box: get1DTable())
			if(box.getPlayer() == player)
				boxes.add(box);
		
		return sqAr(boxes);
	}
	
	//return boxes with value out of given grid coordinates
	public Square[] getBoxMatches(Player player, int[][] coords) {
		ArrayList<Square> boxes = new ArrayList<Square>();
		
		for(int i = 0; i < coords.length; i++) {
			Square current = table[coords[i][0]][coords[i][1]];
			if(current.getPlayer() == player)
				boxes.add(current);
		}
		
		return sqAr(boxes);
	}
	
	public Square[] getBoxMatches(Player player, Square[][] sets)//originally called lines, not sets
	{
		ArrayList<Square> boxes = new ArrayList<Square>();
		
		for(Square[] set: sets)
			for(Square box: set)
				if(box.getPlayer() == player)
					boxes.add(box);
		
		return sqAr(boxes);
	}
	
	public Square[] getBoxMatches(Square[] set1, Square[] set2) {
		ArrayList<Square> matches = new ArrayList<Square>();
		
		for(Square box1: set1)
			for(Square box2: set2)
				if(box1.getPlayer() == null && box1 == box2)
					matches.add(box1);
		
		return sqAr(matches);
	}
	
	public ArrayList<Square> getBoxMatches(Square[] boxes, int[][] coordinates) {
		ArrayList<Square> matches = new ArrayList<Square>();
		
		for(int[] coord: coordinates) {
			for(Square box: boxes)
				if(table[coord[0]][coord[1]] == box)
					matches.add(box);
		}
		
		return matches;
	}
	
	//return a random box in group, that has value
	public Square ranSquare(Square[] group, Player player) {
		ArrayList<Square> boxes = new ArrayList<Square>();
		for(Square box: group)
			if(box.getPlayer() == player)
				boxes.add(box);
		
		return ranSquare(sqAr(boxes));
	}
	
	public Square ranSquare(Square[] squares) {
		return squares.length == 0 ? null : squares[(int)(Math.random()*squares.length)];
	}
	public Square ranSquare(Player player) {
		return ranSquare(getBoxMatches(player));
	}
	public Square ranSquare() {
		return ranSquare(get1DTable());
	}
	
	public void newGame() {
		for(Square[] line: table)
			for(Square box: line)
				box.setPlayer(null);
	}
	
	public Square[] sqAr(ArrayList<Square> list) {
		return list.toArray(new Square[0]);
	}
	
	//for debugging
	public void printGrid() {
		System.out.println("Box states:");
		for(Square[] line: table) {
			for(Square box: line)
				System.out.print(box.getPlayer() + "\t");
			System.out.println();
		}
	}
	
	private void printArL(ArrayList<Square> squares) {
		for(Square box: squares)
			System.out.println(box);
	}
}
