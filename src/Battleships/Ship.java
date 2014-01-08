package Battleships;

import java.awt.Color;

public class Ship{		
		private String name;
		int startx;
		int starty;
		private int length;
		boolean vertikal;
		boolean destroyed = false;
		private int hit;
		int i = 0;
		
		public Ship(int startx, int starty, int length, boolean vertikal){
			this.startx = startx;
			this.starty = starty;
			this.length = length;
			this.vertikal = vertikal;
		}
		public Ship(int length){
			this.length = length;
		}
		public void updateBattlefield(Square[][] game){
			for(int i = 0;i<length;i++){
				if(vertikal){
					game[starty-1][startx-1].setShip(this);
					game[starty-1][startx-1].setBackground(Color.GREEN);
					starty++;					
				}else{
					game[starty-1][startx-1].setShip(this);
					game[starty-1][startx-1].setBackground(Color.GREEN);
					startx++;
				}				
			}
		}
		public boolean isVertical(){
			return vertikal;
		}
		public String getName()
		{
			return this.name;
		}		
		public void bomb(){
			length-=1;
			if(length==0){
				destroyed=true;
			}
		}
		public boolean isDestroyed(){
			return destroyed;
		}
		public void hit(){
			hit++;
		}
		public int getLength()
		{
			return this.length;
		}
		
		public int getX()
		{
			return this.startx;
		}
		
		public int getY()
		{
			return this.starty;
		}
}
