import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Square {
	
	private Player player;
	private int x, y, size, xSpot, ySpot;
	
	public Square(int x, int y, int size, int yPos, int xPos) {
		player = null;
		this.size = size;
		this.x = x;
		this.y = y;
		xSpot = xPos;
		ySpot = yPos;
	}
	
	public void drawMe(Graphics g) {
		if(player != null)
			g.drawImage(player.getSymbol(), x, y, null);
	}
	
	public boolean clickable(int cX, int cY) {
		return player == null && cX > x && cX < x + size && cY > y && cY < y + size;
	}
	
	public void setPlayer(Player player) {this.player = player;}
	
	public Player getPlayer() {return player;}
	public int centerX() {return x+size/2;}
	public int centerY() {return y+size/2;}
	public int getRow() {return ySpot;}
	public int getCol() {return xSpot;}
	
	public String toString() {
		return "position:[" + ySpot + "][" + xSpot + "]; player:" + (player == null ? "none" : player.getName()) + "; x:" + x + "; y:" + y + "; size:" + size;
	}
}
