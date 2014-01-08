package Battleships;

import java.awt.Color;
import javax.swing.JButton;

/**
 * class containing definition of Square and constructor for squares.
 *
 */
@SuppressWarnings("serial")
public class Square extends JButton{
	boolean hasShip = false;
	boolean bombedSquare = false;
	int[][] coord;
	int x=0;
	int y=0;
	Ship s;
	
	public Square(int x, int y){
		this.x = x;
		this.y = y; 
	}
	public void setShip(Ship s){
		hasShip = true;
		this.s = s;
	}
	public void bomb(){
		bombedSquare = true;
		if(hasShip){
		s.bomb();
		}
	}
	public boolean containsDestroyedShip(){
		return s.isDestroyed();
	}	
	public void update(){
		State state = getState();
		switch(state){
		case DESTROYEDSHIP: this.setBackground(Color.BLACK);
			break;
		case BOMBEDSHIP: this.setBackground(Color.RED);
			break;
		case NOSHIP: this.setBackground(Color.GRAY);
			break;
		case NOTBOMBED: this.setBackground(Color.BLUE);
			break;
		}
	}
	public void reset(){
		hasShip = false;
		bombedSquare = false;
		s = null;
	}

	public State getState(){
		if(bombedSquare){
			if(!hasShip){
				return State.NOSHIP;
			}else{
				if(s.isDestroyed()){
				return State.DESTROYEDSHIP;
				}else{
					return State.BOMBEDSHIP;
				}
			}
		}else{
			return State.NOTBOMBED;
		}
		
	}
}

