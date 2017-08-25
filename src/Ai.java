import javax.swing.Timer;
import java.awt.image.BufferedImage;

public class Ai extends Player {
	
	public static int AI_DELAY = 1000; // time sleeping before making move, in milliseconds.
	
	private int level;
	
	public Ai(String name, BufferedImage symbol, BufferedImage icon) {
		super(name, true, symbol, icon);
		level = Rules.numDiffs - 1;
	}
	
	public void doTurn(Game game) {
		Timer t = new Timer(AI_DELAY, e -> chooseSquare(game));
		t.setRepeats(false);
		t.start();
	}
	
	protected void chooseSquare(Game game) {
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
		
		game.playerMove(this, playThis.centerX(), playThis.centerY());
	}
	
	public void setDifficulty(int diff) {
		if(diff >= 0 && diff <= Rules.numDiffs)
			level = diff;
	}
	
	public int getDifficulty() { return level; }
}
