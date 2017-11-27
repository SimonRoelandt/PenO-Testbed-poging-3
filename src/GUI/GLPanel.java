package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import game.DummyGame;

public class GLPanel extends JPanel {
	DummyGame dummyGame;
	Graphics g;
    public GLPanel(DummyGame dummyGame) {
    	//setBackground(Color.blue);
    	this.dummyGame = dummyGame;
    }

    @Override 
    public void paintComponent(Graphics g) {
    	this.g= g;
        super.paintComponent(g);
        //write either "RED" or "BLUE" using graphics
        /*
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 60));
        g.drawString("rood", getWidth() / 2 - 4 / 2,
                getHeight() / 2 + g.getFontMetrics().getHeight() / 2);
        */
        g.drawImage(dummyGame.renderer.screenshot, 0, 0, this);
        System.out.println("SCREENSHOT: " + dummyGame.renderer.screenshot);
    }
    
    public void update() {
    	 paintComponent(g);
    	 System.out.println("update " + g);
    }
}
