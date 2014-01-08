package Battleships;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.*;

/**
 * Environment for the two Battleship-boards aswell as the Log-Container, 
 * where the two boards and Log are drawn in separate windows.
 */
public class GUI implements ActionListener, Runnable {
	String playerOne = "Spelare Ett", playerTwo = "Spelare Två";
	String accuracyOne, accuracyTwo;
	boolean	readyOne = false, readyTwo=false;
	protected boolean gameEnded = false;
	int turn = 0, nptValue = 0;
	protected int playerOneID = 0, playerTwoID=1; 
	Battlefield[] players = new Battlefield[2];

	JTabbedPane tabbedPane;
	JMenuBar menubar;
	Container gameState;
	private static Container LogContainer;
	private static JScrollPane LogScrollPane;
	private static JTextArea Log;
	public JMenu mainMenu, tools;
	public JMenuItem newGame, endGame, onePlayer, twoPlayer, about, help;

	/**
	 * Method for setting up the two tabs and log.
	 * @param pane
	 */
	public void addComponentToPane(Container pane) {
		
		//Pane for the two battlefields
		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(300,300));	
		players[0] = new Battlefield(0, this);
		players[1] = new Battlefield(1, this); 
		tabbedPane.addTab(playerOne, players[0]);
		tabbedPane.addTab(playerTwo, players[1]);

		//Container for log
		LogContainer = new Container();
		LogContainer.setBounds(800, 170, 218, 250);
		Log = new JTextArea("Välj speltyp..");
		Log.setEditable(false);
		Log.setLineWrap(true);
		Log.setWrapStyleWord(true);
		LogScrollPane = new JScrollPane(Log);
		LogScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		LogScrollPane.setBounds(0, 0, 208, 250);
		LogContainer.add(LogScrollPane);

		pane.add(LogContainer);		
		pane.add(tabbedPane, BorderLayout.WEST);
	}
	/**
	 * Method for updating logcontainer
	 */
	public static void addLog(String line) {
		Log.append("\n" + line);
		Log.setCaretPosition(Log.getDocument().getLength());
		Log.moveCaretPosition(Log.getDocument().getLength());
	}

	/**
	 * Method for managing and determining the next player and displaying current Game stats.
	 */
	public void nextPlayer(boolean delay){
		int index = tabbedPane.getSelectedIndex()+1;

		if (index > 1) index = 0;
		nptValue = new Random().nextInt(2147483647); //creates a random "integer-ID" for this specific thread.
		turn = index;
		if (delay) {
			String accuracyOne = "0 %", accuracyTwo = "0 %";
			if (players[index].tries > 0){
				accuracyTwo = getAccuracy(playerOneID);
				accuracyOne = getAccuracy(playerTwoID);
			}
			addLog("Nuvarande ställning:\n");
			addLog(playerOne+ "'s Träffar:\n" +accuracyOne);
			addLog(players[playerOneID].shipsLeft+" Båtar kvar\n");
			addLog(playerTwo+ "'s Träffar:\n" + accuracyTwo);
			addLog(players[playerTwoID].shipsLeft+" Båtar kvar\n");
			addLog('\n'+playerTurn(index) + "'s tur!"+'\n'+'\n');
			Thread nextPlayerThread = new Thread((Runnable)this);
			nextPlayerThread.start();
		} else {
			if (players[turn].computer) {
				index = 1;
				turn = index;
			}
			tabbedPane.setSelectedIndex(index);
		}
	}
	/**
	 * Method for printing the corrent players turn.
	 * @param index
	 * @return
	 */
	public String playerTurn(int index){
		if(index==0){
			return playerTwo;
		}else{
			return playerOne;
		}
	}

	/**
	 * Method for handling and sleeping Threads when switching tabs after
	 * a player bomb its opponent for more user-friendly visualization.
	 */
	public void run() {
		try {
			int tmpVal = nptValue;
			Thread.sleep(1000);
			if (turn == 0 && players[turn+1].computer) {
				tabbedPane.setSelectedIndex(turn);
				players[turn].computerPlay();
			} else {
				if (tmpVal != nptValue) return; //checks if the threads ID is the same as before going to sleep.
				tabbedPane.setSelectedIndex(turn);
			}

		} catch (InterruptedException e) {}
	}
	/**
	 * Method for clearing log
	 */
	public void clearLog(){
		addLog(""+'\n'+'\n'+'\n'+'\n'+'\n'+'\n'+
				'\n'+'\n'+'\n'+'\n'+'\n'+'\n'+
				'\n'+'\n'+'\n'+'\n'+'\n'+'\n');
	}
	/**
	 * Method for creating the user interface and packaging it.
	 */
	private void createAndShowGUI() {
		JFrame frame = new JFrame("Battleships Version 1.2");
		frame.setPreferredSize(new Dimension(520,300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addComponentToPane(frame.getContentPane());

		menu();
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		menubar.add(mainMenu);
		menubar.add(tools);

		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	/**
	 * Method for creating the menusystem.
	 * @return
	 */
	public void menu(){
		mainMenu = new JMenu("Arkiv");
		tools = new JMenu("Verktyg");
		newGame = new JMenu("Nytt spel");
		endGame = new JMenuItem("Avsluta");
		onePlayer = new JMenuItem("1 Spelare");
		twoPlayer = new JMenuItem("2 Spelare");
		about = new JMenuItem("Om Battleships");
		help = new JMenuItem("Hjälp");
		mainMenu.add(newGame);
		mainMenu.add(endGame);
		tools.add(help);	
		tools.add(about);
		newGame.add(onePlayer);
		newGame.add(twoPlayer);
		newGame.addActionListener(this);
		endGame.addActionListener(this);
		onePlayer.addActionListener(this);
		twoPlayer.addActionListener(this);
		about.addActionListener(this);
		help.addActionListener(this);
	}

	/**
	 * ActionListeners for Menubuttons
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == endGame){
			System.exit(0);
		}
		if(e.getSource() == onePlayer || e.getSource() == twoPlayer){
			readyOne=false;
			readyTwo=false;
			turn = 0;
			players[0].newGame();
			players[1].newGame();
			players[1].computer = false;
			tabbedPane.setSelectedIndex(0);
			clearLog();

			if(e.getSource() == onePlayer) {
				setPlayerName(JOptionPane.showInputDialog(null, "Mata in ditt namn."),0);
				playerTwo = "Dator";
				players[1].computerPlaceShips();	
				tabbedPane.addTab(playerOne, players[0]);
				tabbedPane.addTab(playerTwo, players[1]);
			}else{
				setPlayerName(JOptionPane.showInputDialog(null, "Spelare Ett, mata in ditt namn."),0);
				setPlayerName(JOptionPane.showInputDialog(null, "Spelare Två, mata in ditt namn."),1);
				tabbedPane.addTab(playerOne, players[0]);
				tabbedPane.addTab(playerTwo, players[1]);				
			}
		}else if(e.getSource() == help){
			try {
				new Viewer("README.html").setResizable(false);
			} catch (IOException e1){}
		}else if(e.getSource()== about){
			 JOptionPane.showMessageDialog(null, "Ett projekt i kursen Objektorienterad programmering\n" +
												"       Av Emil Bergwik\n" +
												"               Copyright© 2009-2010", "Om Battleships",1);
		}
	}
	/**
	 * Returns Accuracy for any given player.
	 * @param turn
	 * @return String
	 */
	public String getAccuracy(int turn){
		return String.format("%.1f", ((double)players[turn].hits/(double)players[turn].tries)*100) + " %";
	}
	/**
	 * Sets a player's name.
	 * @param name
	 * @param i
	 */
	public void setPlayerName(String name, int i){
		if(i==0){
			this.playerOne = name;
		}else{
			this.playerTwo = name;
		}
	}
	/**
	 * Gets a player's name.
	 * @param turn
	 * @return String
	 */
	public String getPlayerName(int turn){
		if(turn==0){
			return playerOne;
		}else{
			return playerTwo;
		}
	}
	public void endGameStats(int winner){
		addLog("Slutgiltig ställning:\n\n");
		addLog(playerOne+ "'s Träffar:\n" +getAccuracy(playerTwoID));
		addLog(players[playerOneID].shipsLeft+" Båtar kvar\n");
		addLog(playerTwo+ "'s Träffar:\n" + getAccuracy(playerOneID));
		addLog(players[playerTwoID].shipsLeft+" Båtar kvar\n");
		addLog("\nVinnaren är "+ getPlayerName(winner)+"!"+'\n');
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e){}

		UIManager.put("swing.boldMetal", Boolean.FALSE);

		new GUI().createAndShowGUI();
	}
}
