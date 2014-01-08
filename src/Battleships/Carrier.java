package Battleships;

public class Carrier extends Ship {
	/**
	 * Constructor for Carrier
	 */
	public Carrier(int x, int y, boolean vertikal) {
		super(x, y, 5, vertikal);		
	}
	public Carrier(){
		super(5);
	}
}
