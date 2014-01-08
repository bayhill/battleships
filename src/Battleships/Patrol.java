package Battleships;

public class Patrol extends Ship{
	
	/**
	 * Constructor for Patrol
	 */
		public Patrol(int x, int y, boolean vertikal) {
			super(x, y, 2, vertikal);			
		}
		public Patrol(){
			super(2);
		}
}
