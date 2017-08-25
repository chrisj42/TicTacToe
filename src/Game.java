import java.awt.Graphics;
import java.util.ArrayList;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.ChangeEvent;

public class Game
{
	private ArrayList<GameListener> listeners = new ArrayList<>();
	
	enum Events {
		START {
			void occur(GameListener gl) {
				gl.gameStarted();
			}
		},
		UPDATE {
			void occur(GameListener gl) {
				gl.gameUpdated();
			}
		},
		END {
			void occur(GameListener gl) {
				gl.gameEnded();
			}
		};
		
		Events() {}
		
		void occur(GameListener gl) {}
	};
	
	private Grid grid;
	
	private Player curPlayer;
	private Player player1, player2;
	private int totalGamesPlayed;
	
	private boolean playing;//, aiPlaying;
	private String status;
	
	public Game()
	{
		playing = false;
		//aiPlaying = false;
		curPlayer = null;
		totalGamesPlayed = 0;
		
		status = "Choose a  game mode";
		
		grid = new Grid(0, 0, Screen.dimY);
	}
	
	public void drawGrid(Graphics g) {
		grid.drawMe(g);
	}
	
	public void playerMove(int x, int y) {
		if(curPlayer != null && !curPlayer.isComputer())
			playerMove(curPlayer, x, y);
	}
	public void playerMove(Player player, int x, int y) {
		if(playing && player != null && curPlayer == player && grid.mark(player, x, y))
			nextPlayer();
		else if(Runner.debug) {
			System.out.print("GAME: player move request from " + player.getName() + " ignored b/c ");
			if(!playing) System.out.print("game is not running");
			else if(player == null) System.out.print("player is null");
			else if(curPlayer != player) System.out.print("player is not the current player");
			else System.out.print("grid position was taken or invalid");
			System.out.println();
		}
	}
	
	public Player getNextPlayer() {
		return curPlayer == player1 ? player2 : player1;
	}
	
	private void nextPlayer() {
		broadcastEvent(Events.UPDATE);
		
		if(grid.getTokenSets(curPlayer, 3).length > 0)
			endGame(curPlayer);
		else if(grid.getBoxMatches(null).length == 0)
			endGame(null);
		else
			curPlayer = getNextPlayer();
		
		broadcastEvent(Events.UPDATE);
		
		if(playing)
			curPlayer.doTurn(this);
	}
	
	public void newGame() { newGame(player1, player2); }
	public void newGame(Player p1, Player p2)
	{
		if(Runner.debug) System.out.println("GAME: new game; clearing board");
		
		grid.newGame();
		
		player1 = p1;
		player2 = p2;
		
		curPlayer = player1;//Math.random() * 2 == 0 ? player1 : player2;  
		playing = true;
		
		broadcastEvent(Events.START);
		
		curPlayer.doTurn(this);
	}
	
	private void endGame(Player winner)
	{
		if(Runner.debug) System.out.println("GAME: end game.");
		
		playing = false;
		totalGamesPlayed++;
		
		if(winner != null){
			status = winner.getName() + " wins!";
			winner.addScore();
		}
		else
			status = "It's a Tie.";
		
		broadcastEvent(Events.END);
	}
	
	public Grid getGrid() { return grid; }
	
	public Player getCurPlayer() { return curPlayer; }
	public Player getPlayer1() { return player1; }
	public Player getPlayer2() { return player2; }
	
	public int getTotalGames() { return totalGamesPlayed; }
	public String getStatusMessage() { return status; }
	
	//public boolean aiPlaying() { return aiPlaying; }
	public boolean playing() { return playing; }
	
	/*public void computerPlay()
	{
		while(true){
			System.out.print("");
			if(aiPlaying){
				//System.out.println("ai play");
				aiPlay();
				aiPlaying = false;
				nextPlayer();
			}
			broadcastEvent(Events.UPDATE);
		}
	}*/
	
	public void addListener(GameListener gl) {
		listeners.add(gl);
	}
	
	private void broadcastEvent(Events e) {
		for(GameListener gl: listeners) {
			e.occur(gl);
		}
		
		if(e != Events.UPDATE)
			broadcastEvent(Events.UPDATE);
	}
}