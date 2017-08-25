import javax.swing.*;

public class Runner
{
	
	public static boolean debug = false;
	
	public static void main(String[] args)
	{
		boolean keepBs = false;
		int pauseTime = 1000;
		boolean autoReplay = false;
		
		if(args.length > 0 && args[0].equals("?")) {
			String msg = "----------------\nCommand Line Parameter Help:\n";
			msg += "\n\tjava Runner [debug [keepBtns [pauseTime [autoReplay]]]]\n";
			msg += "\nParameter Descriptions:";
			msg += "\n\n   <boolean> debug (default: "+debug+") -- Determines whether to show print statements.";
			
			msg += "\n\n   <boolean> keepBtns (default: "+keepBs+") -- Determines if start game buttons are hidden during gameplay.";
			
			msg += "\n\n   <int> pauseTime (default: "+pauseTime+") -- The time to pause while the Ai \"decides\" what move to make, in miliseconds.";
			
			msg += "\n\n   <boolean> autoReplay (default: "+autoReplay+") -- Enabling this causes a new game to start automatically after the previous one finishes, with the same players.";
			msg += "\n\tGenerally only useful for Ai vs. Ai runs. A button is added if enabled, so it can be toggled in-game.";
			
			msg += "\n----\nAll parameters are optional, but must be in order. Use \"-\" to use the default value for a parameter.";
			
			System.out.println(msg+"\n");
			System.exit(0);
		}
		
		JFrame frame = new JFrame("Tic-Tac-Toe");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		Screen sc;
		
		if(args.length > 0 && !args[0].equals("-"))
			debug = Boolean.valueOf(args[0]);
		if(args.length > 1 && !args[1].equals("-"))
			keepBs = Boolean.valueOf(args[1]);
		if(args.length > 2 && !args[2].equals("-"))
			pauseTime = Integer.valueOf(args[2]);
		if(args.length > 3 && !args[3].equals("-"))
			autoReplay = Boolean.valueOf(args[3]);
		
		Ai.aiTime = pauseTime;
		
		if(debug)
			System.out.println("debug mode");
		
		sc = new Screen(keepBs, autoReplay);
		
		frame.add(sc);
		frame.pack();
		frame.setVisible(true);
	}
}
