import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.io.File;
import java.io.IOException;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Screen extends JPanel implements MouseListener, ActionListener, GameListener
{
	public static final int dimX = 900, dimY = 600;
	
	private static BufferedImage xPic, sX, oPic, sO;
	
	static {
		try {
			xPic = ImageIO.read(new File("resources/X.png"));
			sX = ImageIO.read(new File("resources/smallX.png"));
			oPic = ImageIO.read(new File("resources/O.png"));
			sO = ImageIO.read(new File("resources/smallO.png"));
		} catch(IOException ex1) {
			try {
				xPic = ImageIO.read(new File("X.png"));
				sX = ImageIO.read(new File("smallX.png"));
				oPic = ImageIO.read(new File("O.png"));
				sO = ImageIO.read(new File("smallO.png"));
			} catch(IOException ex2) {
				System.err.println("couldn't read image files:");
				ex2.printStackTrace();
			}
		}
	}
	
	private static final Player player1 = new Player("Player 1", false, xPic, sX);
	private static final Player player2 = new Player("Player 2", false, oPic, sO);
	private static final Ai computer1 = new Ai("Computer 1", xPic, sX);
	private static final Ai computer2 = new Ai("Computer 2", oPic, sO);
	
	private Game game;
	
	private JButton newPvPGame, newPvCGame, newCvPGame, newCvCGame, toggleReplay;
	private JSlider difficulty;
	
	private boolean keepBtns, autoReplay;
	
	public Screen(boolean keepBtns, boolean autoStart)
	{
		setLayout(null);
		
		game = new Game();
		
		this.keepBtns = keepBtns;
		autoReplay = autoStart;
		
		newPvPGame = createButton("Player vs. Player", dimX-220, 190);
		newPvCGame = createButton("Player vs. Computer", dimX-220, 230);
		newCvPGame = createButton("Computer vs. Player", dimX-220, 270);
		newCvCGame = createButton("Computer vs. Computer", dimX-220, 310);
		if(autoReplay || Runner.debug)
			toggleReplay = createButton("Toggle auto-replay", dimX-220, 140);
		
		difficulty = new JSlider(0, Rules.numDiffs - 1, Rules.numDiffs - 1);
		difficulty.setBounds(dimX-220, 380, 200, 50);
		difficulty.setMajorTickSpacing(1);
		difficulty.setPaintTicks(true);
		difficulty.setPaintLabels(true);
		add(difficulty);
		
		setFocusable(true);
		addMouseListener(this);
		
		game.addListener(this);
	}
	
	public Dimension getPreferredSize() {return new Dimension(dimX, dimY);}
	
	private JButton createButton(String title, int boundX, int boundY)
	{
		JButton button = new JButton(title);
		button.setBounds(boundX, boundY, 200, 30);
		button.addActionListener(this);
		add(button);
		return button;
	}
	
	//private int txtWidth(Font f, String str) {return this.getFontMetrics(f).stringWidth(str);}
	//private int txtHeight(Font f) {return this.getFontMetrics(f).getHeight();}
	
	private int txtWidth(Graphics g, String str) {return g.getFontMetrics().stringWidth(str);}
	private int txtHeight(Graphics g) {return g.getFontMetrics().getHeight();}
	
	//private int xTextPos(Graphics g, String text, int offset) {return (dimX+offset-txtWidth(g, text))/2;}
	private int centerText(Graphics g, String text, int leftX, int rightX) {return leftX + (rightX-leftX-txtWidth(g, text))/2;}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Font title = new Font("Arial", Font.BOLD, 25);
		Font msg = new Font("Arial", Font.PLAIN, 20);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, dimX, dimY);
		g.setColor(Color.black);
		
		game.drawGrid(g);
		
		g.setColor(Color.blue);
		g.setFont(msg);
		
		int p1Score = game.getPlayer1() == null ? 0 : game.getPlayer1().getScore();
		int p2Score = game.getPlayer2() == null ? 0 : game.getPlayer2().getScore();
		String text1 = "Player 1 Score: " + p1Score;
		String text2 = "Player 2 Score: " + p2Score;
		g.drawString(text1, dimX-5-txtWidth(g, text1), dimY-120);
		g.drawString(text2, dimX-5-txtWidth(g, text2), dimY-80);
		
		String games = "Games Played: " + game.getTotalGames();
		g.drawString(games, dimX-5-txtWidth(g, games), dimY-20);
		
		Rectangle hud = new Rectangle(game.getGrid().getSize(), 25, dimX-game.getGrid().getSize(), dimY-25);
		Rectangle textArea = new Rectangle(hud.x + hud.width / 5, hud.y, hud.width * 3 / 5, hud.height);
		
		g.setFont(msg.deriveFont(18f));
		String compDif = "Computer Difficulty:";
		g.drawString(compDif, centerText(g, compDif, dimX-220, dimX-20), 375);
		
		g.setFont(title);
		
		if(game.playing()) {
			String message = game.getCurPlayer().getName() + "'s  turn";
			
			BufferedImage miniSym = game.getCurPlayer().getIcon();
			int msgHeight = writeParagraph(g, message, true, textArea);
			
			g.drawImage(miniSym, textArea.x - miniSym.getWidth() - 5, textArea.y + msgHeight / 2, null);
			g.drawImage(miniSym, textArea.x + textArea.width + 5, textArea.y + msgHeight / 2, null);
		} else
			writeParagraph(g, game.getStatusMessage(), true, textArea);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == toggleReplay) {
			autoReplay = !autoReplay;
			return;
		}
		
		computer1.setDifficulty(difficulty.getValue());
		computer2.setDifficulty(difficulty.getValue());
		
		if(e.getSource() == newPvPGame)
			game.newGame(player1, player2);
		else if(e.getSource() == newPvCGame)
			game.newGame(player1, computer2);
		else if(e.getSource() == newCvPGame)
			game.newGame(computer1, player2);
		else if(e.getSource() == newCvCGame)
			game.newGame(computer1, computer2);
		
		repaint();
	}
	
	public void mouseClicked(MouseEvent e) {
		game.playerMove(e.getX(), e.getY());
		repaint();
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	private int writeParagraph(Graphics g, String text, boolean centered, Rectangle bounds, int lineSpacing) {
		int width = bounds.width, startX = bounds.x, startY = bounds.y + txtHeight(g);
		int totalHeight = 0;
		
		//String method that splits a String phrase into words, since " " is passed in:
		String[] words = text.split(" ");
		int i = 0;//Count integer
		while (i < words.length)
		{
			//String that holds the characters that will be printed on the current line:
			StringBuilder currentLine = new StringBuilder(words[i++]);
			//While loop that runs while the pixel width of the string is less than the width passed in:
			while (( i < words.length ) && (words[i].length() != 0) && (g.getFontMetrics(g.getFont()).stringWidth(currentLine + " " + words[i]) < width))
			{
				currentLine.append(" ").append(words[i]);//Adds as many words as will fit onto the line
				i++;
			}
			if(centered)
				g.drawString(currentLine.toString(), centerText(g, currentLine.toString(), startX, startX+width), startY);
			else
				g.drawString(currentLine.toString(), startX, startY);//Draws the line
			//Gets the height of a standard line of text in the passed in font:
			int oldY = startY;
			startY += txtHeight(g) + lineSpacing;//Increases the y variable to draw on the next line
			totalHeight += startY-oldY;
		}
		
		return totalHeight;
	}
	private int writeParagraph(Graphics g, String text, boolean centered, Rectangle bounds) {
		return writeParagraph(g, text, centered, bounds, 2);
	}
	
	@Override
	public void gameStarted() {
		if(!keepBtns) {
			remove(newPvPGame);
			remove(newPvCGame);
			remove(newCvPGame);
			remove(newCvCGame);
		}
	}
	
	@Override
	public void gameUpdated() { repaint(); }
	
	@Override
	public void gameEnded() {
		if(!autoReplay) {
			add(newPvPGame);
			add(newPvCGame);
			add(newCvPGame);
			add(newCvCGame);
		} else {
			if(Runner.debug) System.out.println("game end; waiting");
			Timer t = new Timer(Runner.REPLAY_DELAY, e -> game.newGame());
			t.setRepeats(false);
			t.start();
		}
		repaint();
	}
}