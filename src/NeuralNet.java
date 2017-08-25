import java.awt.image.BufferedImage;

public class NeuralNet extends Ai {
	
	public NeuralNet(String name, BufferedImage symbol, BufferedImage icon) {
		super(name, symbol, icon);
	}
	
	protected void chooseSquare(Game game) {
		
	}
	
	// neural nets don't really have difficulty levels.
	public void setDifficulty(int diff) {}
}
