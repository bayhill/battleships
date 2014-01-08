package Battleships;

import java.io.*;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
/**
 * Class for showing the README-file.
 *
 */
@SuppressWarnings("serial")
public class Viewer extends JFrame { 
	JEditorPane page = new JEditorPane();
	JScrollPane sp = new JScrollPane(page);
	
	public Viewer(String line) throws IOException{
		line = "file:" + line;
		page.setPage(line);
		setContentPane(sp);
		page.setEditable(false);
		setSize(500,260);
		setTitle("Help");
		setVisible(true);
	}
}
