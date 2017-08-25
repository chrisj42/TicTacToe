import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Ai extends Player {
	
	public static int aiTime = 1000; // time sleeping before making move, in milliseconds.
	public static final int numDiffs = 5;
	
	enum Rules {
		AI_ADVANT (new double[] 	{.00, .99, 1.0, 1.0, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square[] aiAdvant = grid.getBoxMatches(null, grid.getTokenSets(self, 2));
				if(aiAdvant.length > 0) {
					if(Runner.debug)
						System.out.println("test successful.");
					return grid.ranSquare(aiAdvant);
				}
				
				return null;
			}
		},
		
		FOE_ADVANT (new double[] 	{.00, .90, 1.0, 1.0, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square[] foeAdvant = grid.getBoxMatches(null, grid.getTokenSets(opponent, 2));
				if(foeAdvant.length > 0) {
					if(Runner.debug)
						System.out.println("test successful.");
					return grid.ranSquare(foeAdvant);
				}
				
				return null;
			}
		},
		
		OFFENSE (new double[] 		{.00, .00, .00, .00, 1.0}) {
			// looks for multiple lines of 1 ai token, and plays on a blank space intersecting two of them.
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square[][] loners = grid.getTokenSets(self, 1, true);
				ArrayList<Square> matches = new ArrayList<>();
				
				for(int set1 = 0; set1 < loners.length-1; set1++)
					for(int set2 = set1+1; set2 < loners.length; set2++)
						matches.addAll(Arrays.asList(grid.getBoxMatches(loners[set1], loners[set2])));
				
				// if(Runner.debug) {
				// 	System.out.print("matches Array: [");
				// 	for(Square box: matches) System.out.print("r"+box.getRow()+"c"+box.getCol() + ", ");
				// 	System.out.println("]");
				// }
				
				return grid.ranSquare(grid.sqAr(matches), null);
			}
		},
		
		DEFENSE (new double[] 		{.00, .10, .00, .50, 1.0}) {
			// looks for multiple lines of 1 foe token, and plays on a blank space intersecting two of them.
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square[][] loners = grid.getTokenSets(opponent, 1, true);
				ArrayList<Square> matches = new ArrayList<>();
				
				for(int set1 = 0; set1 < loners.length-1; set1++)
					for(int set2 = set1+1; set2 < loners.length; set2++)
						matches.addAll(Arrays.asList(grid.getBoxMatches(loners[set1], loners[set2])));
				
				// if(grid.getSquare(1, 1).getPlayer() == opponent)
				// 	matches = grid.getBoxMatches(grid.sqAr(matches), grid.cornerCoords);
				
				if(Runner.debug) {
					System.out.print("matches Array: [");
					for(Square box: matches) System.out.print("r"+box.getRow()+"c"+box.getCol() + ", ");
					System.out.println("]");
				}
					
				return grid.ranSquare(grid.sqAr(matches), null);
			}
		},
		
		MID_SQUARE (new double[] 	{.00, .00, .40, .80, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square middle = grid.getSquare(1, 1);
				if(middle.getPlayer() == null) {
					if(Runner.debug)
						System.out.println("Test successful.");
					return middle;
				}
				
				return null;
			}
		},
		
		OPPOSITE_CORNER (new double[] {.00, .15, .45, .60, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				/*Square[] boxes = grid.getBoxMatches(opponent, grid.cornerCoords);
				for(Square box: boxes) {
					int row = (box.getRow() + 1) / 2;
					int col = (box.getCol() + 1) / 2;
					Square poss = grid.getSquare((row+2)%3, (col+2)%3);
					if(poss.getPlayer() == null) {
						if(Runner.debug) System.out.println("opp.corner test success.");
						return poss;
					}
				}
				
				if(Runner.debug)System.out.println("opp. corner test failed.");
				return null;*/
				Square[] foeCorners = grid.getBoxMatches(opponent, grid.cornerCoords);
				for(int i = 0; i < foeCorners.length; i++)
					foeCorners[i] = grid.getSquare(foeCorners[i].getRow()==0?2:0, foeCorners[i].getCol()==0?2:0);
				
				return grid.ranSquare(foeCorners, null);
			}
		},
		
		NEAR_CORNER (new double[] 	{.00, .20, .60, .75, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				for(Square box: grid.getBoxMatches(opponent, grid.sideCoords)) {
					int rand = (int) (Math.random()*2) - 1;
					if(rand == 0) rand = 1;
					//System.out.println(rand);
					Square poss1 = grid.getSquare(box.getRow(), 1+rand);
					Square poss2 = grid.getSquare(1+rand, box.getCol());
					if(box.getRow() != 1 && poss1.getPlayer() == null)
						return poss1;
					if(box.getCol() != 1 && poss2.getPlayer() == null)
						return poss2;
				}
				
				return null;
			}
		},
		
		OPEN_CORNER (new double[] 	{.00, .00, .20, .75, 1.0}) {
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				Square[] openCorners = grid.getBoxMatches((Player)null, grid.cornerCoords);
				if(openCorners.length > 0) {
					if(Runner.debug)
						System.out.println("test successful.");
					return grid.ranSquare(openCorners);
				}
				
				return null;
			}
		},
		
		ALL_OPEN (new double[] {1.0, 1.0, 1.0, 1.0, 1.0}) {
			// this comes into play if the computer moves first.
			@Override
			Square testRule(Grid grid, Player self, Player opponent) {
				if(grid.getBoxMatches(null).length == grid.get1DTable().length)
					return grid.ranSquare();
				
				return null;
			}
		};
		
		private double[] useChance;
		
		Rules(double[] chances) {
			useChance = chances;
		}
		
		// this is the public method, and might not even execute the actual rule behavior.
		public Square test(Grid grid, Ai self, Player opponent) {
			if(Runner.debug) System.out.println("testing rule: " + this);
			if(Math.random() < useChance[self.level]) {
				Square sq = testRule(grid, self, opponent);
				if(Runner.debug)
					System.out.println(sq == null ? "Test failed." : "Test successful.");
				return sq;
			} else if(Runner.debug)
				System.out.println("skipped rule " + this);
			
			return null;
		}
		
		// this is what actually performs the test.
		Square testRule(Grid grid, Player self, Player opponent) {
			return null;
		}
		
		public static final Rules[] values = {
				AI_ADVANT, FOE_ADVANT, OFFENSE, DEFENSE, ALL_OPEN, MID_SQUARE, OPPOSITE_CORNER, NEAR_CORNER, OPEN_CORNER 
		};
	}
	
	
	
	private int level;
	
	public Ai(String name, BufferedImage symbol, BufferedImage icon) {
		super(name, true, symbol, icon);
		level = numDiffs - 1;
	}
	
	public void doTurn(Game game) {
		Timer t = new Timer(aiTime, e -> chooseSquare(game));
		t.setRepeats(false);
		t.start();
	}
	
	private void chooseSquare(Game game) {
		Grid grid = game.getGrid();
		Player opponent = game.getNextPlayer();
		
		Square playThis = null;
		
		for (int i = 0; i < Rules.values.length && playThis == null; i++)
			playThis = Rules.values[i].test(grid, this, opponent);
		
		if(playThis == null) {
			if(Runner.debug)
				System.out.println("choosing random square.");
			playThis = grid.ranSquare((Player)null);
		}
		
		if(Runner.debug)
			System.out.println("\ncomputer's Square: " + playThis);
		
		//if(Runner.debug) System.out.println("grids same: " + (grid == game.getGrid()));
		
		game.playerMove(this, playThis.centerX(), playThis.centerY());
	}
	
	public void setDifficulty(int diff) {
		if(diff >= 0 && diff <= numDiffs)
			level = diff;
	}
}
