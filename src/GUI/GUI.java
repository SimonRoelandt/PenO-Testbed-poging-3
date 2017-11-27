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
	LabelPanel positie, hpr, snelheid, versnelling;
	
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
		
	
		positie = new LabelPanel("positie", frame);
		hpr = new LabelPanel("hpr", frame);
		snelheid =  new LabelPanel("snelheid", frame);
		versnelling = new LabelPanel("versnelling", frame);
		
		
		
		
		
		//panelen om in te vullen: 
		panelWingX = addLabelTextPanel("WingX", dummyGame.drone.wingX);
		panelWingX.tf.addActionListener(new ListenForWingX());
		paneltailsize = addLabelTextPanel("Tail Size", dummyGame.drone.tailSize);
		paneltailsize.tf.addActionListener(new ListenForTailSize());
		panelenginemass = addLabelTextPanel("Engine Mass", dummyGame.drone.engineMass);
		panelenginemass.tf.addActionListener(new ListenForEngineMass());
		panelwingmass = addLabelTextPanel("Wing Mass", dummyGame.drone.wingMass);
		panelwingmass.tf.addActionListener(new ListenForWingMass());
		paneltailmass = addLabelTextPanel("Tail Mass", dummyGame.drone.tailMass);
		paneltailmass.tf.addActionListener(new ListenForTailMass());
		panelmaxthrust = addLabelTextPanel("Max Thrust", dummyGame.drone.maxThrust);
		panelmaxthrust.tf.addActionListener(new ListenForMaxThrust());
		panelmaxaoa = addLabelTextPanel("Max AOA", dummyGame.drone.maxAOA);
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
		
		
		update();
		
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
	
	
	public LabelTextPanel addLabelTextPanel(String text, Float value) {
		return new LabelTextPanel(text, value, frame);
	}
	public ButtonPanel addButtonPanel(String text) {
		return new ButtonPanel(text, frame);
	}
	
	public void update() {
		positie.label.setText("positie:         " + dummyGame.drone.getPositionInWorld().x +
				"      "+ dummyGame.drone.getPositionInWorld().y + "      "
				+ dummyGame.drone.getPositionInWorld().z + "      ");
		hpr.label.setText("HEADING PITCH ROLL:           " + dummyGame.drone.getHeading() +
				"      "+ dummyGame.drone.getPitch()+ "      "
				+ dummyGame.drone.getRoll() + "      ");
		snelheid.label.setText("SNELHEID:           " + dummyGame.drone.getVelocityInWorld().x+
				"      "+ dummyGame.drone.getVelocityInWorld().y+ "      "
				+ dummyGame.drone.getVelocityInWorld().z + "      ");
		versnelling.label.setText("VERSNELLING:           " + dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).x +
				"      "+ dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).y+ "      "
				+ dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).z + "      ");
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
			float input = Float.parseFloat(panelWingX.tf.getText());
			dummyGame.drone.wingX = input;
		}
	}
	
	private class ListenForTailSize implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailsize.tf.getText());
			dummyGame.drone.tailSize = input;
		}
	}
	
	private class ListenForEngineMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelenginemass.tf.getText());
			dummyGame.drone.engineMass = input;
		}
	}
	
	private class ListenForWingMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelwingmass.tf.getText());
			dummyGame.drone.wingMass= input;
		}
	}
	
	private class ListenForTailMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailmass.tf.getText());
			dummyGame.drone.tailMass = input;
		}
	}
	
	private class ListenForMaxThrust implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelmaxthrust.tf.getText());
			dummyGame.drone.maxThrust = input;
		}
	}
	
	private class ListenForMaxAOA implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelmaxaoa.tf.getText());
			dummyGame.drone.maxAOA = input;
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
