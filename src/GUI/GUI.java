package GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import game.DummyGame;

public class GUI {
	DummyGame dummyGame;
	private JFrame frame;
	JButton buttonPlane, buttonChase, buttonSide;
	int buttonClicked;				
	JPanel panelViews;
	JLabel label1, label2;
	JTextField textfield1, textfield2;
	LabelTextPanel panelWingX, paneltailsize, panelenginemass, panelwingmass, paneltailmass, panelmaxthrust, panelmaxaoa;
	GLPanel glpanel;
	ButtonPanel buttonStart;
	
	public GUI(DummyGame dummyGame) {
		this.dummyGame = dummyGame;
	}
	
	public void init() {	
		frame = new JFrame("hello");
		frame.setSize(400, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(1400, 20);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		frame.setLayout(new GridLayout(0,1));
		
	
		addLabelTextPanel("snelheid");
		
		//panelen om in te vullen: 
		panelWingX = addLabelTextPanel("WingX");
		panelWingX.tf.addActionListener(new ListenForWingX());
		paneltailsize = addLabelTextPanel("Tail Size");
		paneltailsize.tf.addActionListener(new ListenForTailSize());
		panelenginemass = addLabelTextPanel("Engine Mass");
		panelenginemass.tf.addActionListener(new ListenForEngineMass());
		panelwingmass = addLabelTextPanel("Wing Mass");
		panelwingmass.tf.addActionListener(new ListenForWingMass());
		paneltailmass = addLabelTextPanel("Tail Mass");
		paneltailmass.tf.addActionListener(new ListenForTailMass());
		panelmaxthrust = addLabelTextPanel("Max Thrust");
		panelmaxthrust.tf.addActionListener(new ListenForMaxThrust());
		panelmaxaoa = addLabelTextPanel("Max AOA");
		panelmaxaoa.tf.addActionListener(new ListenForMaxAOA());
	
		//panel met view-buttons
		buttonPlane = new JButton("Plane view");
		buttonPlane.addActionListener(new ListenForPlaneButton());
		buttonChase = new JButton("Chase view");
		buttonChase.addActionListener(new ListenForChaseButton());
		buttonSide = new JButton("Top and Side view");
		buttonSide.addActionListener(new ListenForSideButton());
		
		panelViews = new JPanel();
		panelViews.add(buttonPlane);
		panelViews.add(buttonChase);
		panelViews.add(buttonSide);
		frame.add(panelViews);
		// tot hier
		
		//startknop
		buttonStart = addButtonPanel("start");
		buttonStart.button.addActionListener(new ListenForStartButton());
		
		
		
		
		BufferedImage img = dummyGame.renderer.screenshot;
		Canvas canvas = new Canvas();
		if (img!=null) {
			canvas.getGraphics().drawImage(img, 0,0, null);
			BufferedImage bi = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);  
			Graphics2D g2 = bi.createGraphics();
			canvas.printAll(g2);  
			g2.dispose();
		}
		//LWJGLCanvas canvas = new LWJGLCanvas(dummyGame);
		
		frame.add(canvas/*, BorderLayout.CENTER*/);
		
		
		
	
	
	}
	
	public void run() {
		
		frame.setVisible(true);
	}
	
	
	public LabelTextPanel addLabelTextPanel(String text) {
		return new LabelTextPanel(text, frame);
	}
	public ButtonPanel addButtonPanel(String text) {
		return new ButtonPanel(text, frame);
	}
	
	public void update() {

	}
	
	
	// LISTENERS
	
	private class ListenForMassa implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.cameraTop.setRotation(90f, 90f, 90f);	
		}
	}
	
	private class ListenForPlaneButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "plane";
		}
	}
	
	private class ListenForChaseButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "chase";
		}
	}
	
	private class ListenForSideButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "side";
		}
	}
	
	private class ListenForStartButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.startSimulation = true;
		}
	}
	
	private class ListenForWingX implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForTailSize implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForEngineMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForWingMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForTailMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForMaxThrust implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class ListenForMaxAOA implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	
	
	
	
	
	
	
	
	private class ListenForKeys implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {
		
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		
	}

}
