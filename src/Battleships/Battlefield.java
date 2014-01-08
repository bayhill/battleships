package Battleships;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

/**
 * Battlefield-class, containing method for creating the menu, board and ActionListeners for them. 
 * Also contains some minor help-methods for updating the gridlayout.
 * @author Emil
 *
 */
@SuppressWarnings("serial")
public class Battlefield extends JPanel implements ActionListener {	

	final int ID;
	Square[][] game = new Square[11][11];
	ArrayList<Ship> deployableShips = new ArrayList<Ship>();
	Square FirstPlacement = null; 
	boolean gameStarted = false;
	int shipsLeft = 8;
	int hits=0, tries=0, boatSquares=0;
	boolean computer=false;

	private GUI gui = null;

	/**
	 * Method for creating the gridLayout for each player.
	 * @param gameStarted
	 */
	public void board(boolean gameStarted){
		int o=0;
		for(int t=0;t<11;t++){
			JLabel a = new JLabel(""+t, JLabel.CENTER);
			a.setBorder(new BevelBorder(BevelBorder.RAISED));
			add(a);
		}
		for(int i=0;i<10;i++){
			o++;
			JLabel a = new JLabel(""+o, JLabel.CENTER);
			a.setBorder(new BevelBorder(BevelBorder.RAISED));
			add(a);
			for(int k=0;k<10;k++){
				Square square = new Square(k,i);
				square.addActionListener(this);
				square.setBackground(Color.GRAY);
				square.setBorder(new LineBorder(Color.CYAN, 1));
				square.setEnabled(false);
				add(square);
				game[i][k] = square;
			}
		}
	}
	/**
	 * Constructor for a battlefield.
	 */
	public Battlefield(int ID, GUI gui){
		this.ID = ID;
		this.gui = gui;
		newGame();
		board(false);
		setLayout(new GridLayout(11,11));
		setSize(800,600);
		setPreferredSize(new Dimension(800,600));
		setVisible(true);
	}	
	/**
	 * ActionListeners for Squares
	 */
	public void actionPerformed(ActionEvent e){
		if (e.getSource() != null && e.getSource() instanceof Square) {
			Square button = (Square) e.getSource();
			if (gameStarted) {
				if (gui.turn != this.ID || !gui.readyOne || !gui.readyTwo){
					return;
				}
				State state = button.getState();
				if(state == State.NOTBOMBED){
					button.bomb();
					tries++;
					if(button.hasShip) {
						hits++;
					}
					if(button.hasShip && button.containsDestroyedShip()){
						shipsLeft--;
					}				
					updateSquares();
					if(shipsLeft==0){
						int winner = (gui.turn+1)%2;
						gui.turn = -1; //Disables players from making further moves after game ended.
						JOptionPane.showMessageDialog(null, "Bra gjort "+ gui.getPlayerName(winner)+"!");
						gui.endGameStats(winner);
					} else {
						gui.nextPlayer(true);
					}
				}
			} else {
				/** 
				 * This is where you end up when placing ships.
				 */
				if (FirstPlacement == null) {
					if (button.hasShip) {
						JOptionPane.showMessageDialog(null, "Det ligger redan ett skepp på denna position.");
						return;
					}
					FirstPlacement = button;
					button.setBackground(Color.GREEN);
				} else {
					if (button.x == FirstPlacement.x && button.y == FirstPlacement.y) { // If same square
						FirstPlacement.setBackground(Color.BLUE);
						FirstPlacement = null;
						return; 
					}
					int fromX = FirstPlacement.x+1;
					int fromY = FirstPlacement.y+1;
					int oldX = button.x+1;
					boolean vertical = (fromX == oldX);  

					Ship tmpShip = null;
					for(Ship ship : deployableShips) {
						if (!validMove(fromX, fromY, vertical, ship.getLength())) {
							JOptionPane.showMessageDialog(null, "Inte en giltig placering!");
							FirstPlacement.setBackground(Color.BLUE);
							FirstPlacement = null;
							return;
						}
						tmpShip = ship;
						ship.startx = fromX;
						ship.starty = fromY;
						ship.vertikal = vertical;
						ship.updateBattlefield(game);
						break;
					}
					if (tmpShip == null) {
						gameStarted = true;
						GUI.addLog(gui.getPlayerName(1)+ "'s tur!"+'\n'+'\n');
					} else {
						deployableShips.remove(tmpShip);
						if (deployableShips.size() == 0) {
							JOptionPane.showMessageDialog(null, "Alla dina skepp är nu utlagda.\nGömmer placeringar..");
							gameStarted = true;
							updateSquares();

							if (this.ID == 0) gui.readyOne = true;
							else gui.readyTwo = true;
							gui.nextPlayer(false);
						}
					}

					FirstPlacement = null;
				}
			}
		}
	}
	/**
	 * Determines if the move was valid or not (eg. out of bounds or if square already has ship.
	 * @param x
	 * @param y
	 * @param vertical
	 * @param length
	 * @return
	 */
	public boolean validMove(int x, int y, boolean vertical, int length){
		for(int i=0;i<length;i++){
			if (x > 10) return false;
			if (y > 10) return false;
			if (game[y-1][x-1].hasShip) return false;
			if(vertical){
				y++;
			} else {
				x++;
			}
		}
		return true;
	}
	/**
	 * Creates a new game and resets variables to default.
	 */
	public void newGame(){
		gameStarted = false;
		gui.gameEnded = false;
		shipsLeft = 8;
		hits = 0;
		tries = 0;
		FirstPlacement = null;
		deployableShips.clear();
		shipsForGame();		
		for(int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				if (game[i][j] == null) return;
				game[i][j].reset();
				game[i][j].setEnabled(true);
			}
		}
		updateSquares();
	}
	/**
	 * Method for adding available ships to pool.
	 */
	public void shipsForGame(){
		deployableShips.add(new Carrier());
		deployableShips.add(new Cruiser());
		deployableShips.add(new Hunter());
		deployableShips.add(new Hunter());
		deployableShips.add(new Hunter());
		deployableShips.add(new Patrol());
		deployableShips.add(new Patrol());
		deployableShips.add(new Patrol());	

		boatSquares = 0;
		for (Ship ship : deployableShips) {
			boatSquares += ship.getLength();
		}
	}
	/**
	 * Method for placing Computer Ships.
	 */
	public void computerPlaceShips() {
		computer = true;
		Random r = new Random();
		int left = 8;
		while (left > 0) {
			int x = r.nextInt(10) + 1;
			int y = r.nextInt(10) + 1;
			boolean vertical = r.nextBoolean();
			Ship tmpShip = null;
			for(Ship ship : deployableShips) {
				if (!validMove(x, y, vertical, ship.getLength())) break;
				tmpShip = ship;
				left--;
				ship.startx = x;
				ship.starty = y;
				ship.vertikal = vertical;
				ship.updateBattlefield(game);
				break;
			}
			if (tmpShip != null) deployableShips.remove(tmpShip);
		}

		gameStarted = true;
		gui.readyTwo = true;
		updateSquares();
	}
	/**
	 * Method for Computer-bombing, at the moment totally random. :) (A.K.A AI EASY)
	 */
	public void computerPlay() {
		Random r = new Random();
		while(true){
			int x=r.nextInt(10), y=r.nextInt(10);
			if (game[y][x].getState() == State.NOTBOMBED) {
				game[y][x].bomb();
				tries++;
				if (game[y][x].hasShip) hits++;
				if(game[y][x].hasShip && game[y][x].containsDestroyedShip()){
					shipsLeft--;
				}					
				break;
			}
		}
		updateSquares();

		if(shipsLeft==0){
			gui.turn = -1; //Disables players from making further moves after game ended.
			GUI.addLog("Tyvärr, datorn vann.");
		} else {
			gui.nextPlayer(true);
		} 
	}
	/** 
	 * Short method for updating the gridlayout, so that each square shows its correct colour.
	 */
	public void updateSquares(){
		for(int i=0;i<10;i++){
			for(int k=0;k<10;k++){
				game[i][k].update();
			}
		}
	}
}