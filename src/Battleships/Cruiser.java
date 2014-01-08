package Battleships;

public class Cruiser extends Ship {
	/**
	 * Constructor for Cruiser
	 */
	public Cruiser(int x, int y, boolean vertikal) {
		super(x, y, 4, vertikal);		
	}
	public Cruiser(){
		super(4);
	}
}
