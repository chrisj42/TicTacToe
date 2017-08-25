import java.awt.image.BufferedImage;

public class Player {
	
	private BufferedImage symbol, icon;
	
	private String playerName;
	private int score;
	private boolean isComputer;
	
	public Player(String name, boolean isComputer, BufferedImage symbol, BufferedImage icon) {
		this.playerName = name;
		this.isComputer = isComputer;
		this.symbol = symbol;
		this.icon = icon;
	}
	
	public void doTurn(Game game) {}
	
	public String getName() { return playerName; }
	
	public int getScore() { return score; }
	public void addScore() { score++; }
	
	public boolean isComputer() { return isComputer; }
	
	public BufferedImage getSymbol() { return symbol; }
	public BufferedImage getIcon() { return icon; }
}
