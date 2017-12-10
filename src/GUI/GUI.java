package GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

import javax.swing.border.*;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Engine;

import javax.swing.*;

import game.DummyGame;

public class GUI {
	DummyGame dummyGame;
	private JFrame frame;
	JButton buttonPlane, buttonChase, buttonSide;
	int buttonClicked;				
	JPanel panelViews, panelConfig, panelValues, panelStart, panelGenerate, panelCustomCube;
	JLabel label1, label2;
	JTextField textfield1, textfield2;
	LabelTextPanel panelWingX, paneltailsize, panelenginemass, panelwingmass, paneltailmass, panelmaxthrust, panelmaxaoa, panelXPos, panelYPos, panelZPos;
	GLPanel glpanel;
	ButtonPanel buttonStart, buttonGenerate, buttonChooseFile, buttonCustomCube;
	LabelPanel positie, hpr, snelheid, versnelling, inclinatie, aoa;
	JFileChooser fc;
	JTabbedPane tabbedPaneGenerate;
	
	float xPos, yPos, zPos;
	
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
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		
		panelValues = new JPanel();
		panelValues.setLayout(new GridLayout(5,1));
		Border valuesBorder = BorderFactory.createTitledBorder("Values");
		panelValues.setBorder(valuesBorder);
		positie = new LabelPanel("Position", panelValues);
		hpr = new LabelPanel("Head, Pitch, Roll", panelValues);
		snelheid =  new LabelPanel("Velocity", panelValues);
		versnelling = new LabelPanel("Acceleration", panelValues);
		inclinatie = new LabelPanel("Inclinations left, right, hor, ver", panelValues);
		//aoa = new LabelPanel("Angle of Attack", panelValues);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelValues, c);
		
		
		
		//panelen om in te vullen: 
		panelConfig = new JPanel();
		panelConfig.setLayout(new GridLayout(7,1));
		Border configBorder = BorderFactory.createTitledBorder("Configuration");
		panelConfig.setBorder(configBorder);
		panelWingX = addLabelTextPanelConfig("WingX", dummyGame.drone.wingX);
		panelWingX.tf.addActionListener(new ListenForWingX());
		paneltailsize = addLabelTextPanelConfig("Tail Size", dummyGame.drone.tailSize);
		paneltailsize.tf.addActionListener(new ListenForTailSize());
		panelenginemass = addLabelTextPanelConfig("Engine Mass", dummyGame.drone.engineMass);
		panelenginemass.tf.addActionListener(new ListenForEngineMass());
		panelwingmass = addLabelTextPanelConfig("Wing Mass", dummyGame.drone.wingMass);
		panelwingmass.tf.addActionListener(new ListenForWingMass());
		paneltailmass = addLabelTextPanelConfig("Tail Mass", dummyGame.drone.tailMass);
		paneltailmass.tf.addActionListener(new ListenForTailMass());
		panelmaxthrust = addLabelTextPanelConfig("Max Thrust", dummyGame.drone.maxThrust);
		panelmaxthrust.tf.addActionListener(new ListenForMaxThrust());
		panelmaxaoa = addLabelTextPanelConfig("Max AOA", dummyGame.drone.maxAOA);
		panelmaxaoa.tf.addActionListener(new ListenForMaxAOA());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelConfig, c);
		
		
		
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
		Border viewBorder = BorderFactory.createTitledBorder("Views");
		panelViews.setBorder(viewBorder);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelViews, c);
		// tot hier
		
		//random cubes
		tabbedPaneGenerate = new JTabbedPane();
		panelGenerate = new JPanel();
		buttonGenerate = addButtonPanelGenerate("Generate random cubes");
		buttonGenerate.button.addActionListener(new ListenForGenerate());
		buttonChooseFile = addButtonPanelGenerate("Select a predefined path");
		buttonChooseFile.button.addActionListener(new ListenForFile());
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File("src\\Dragon.obj"));	
		
		
		panelCustomCube = new JPanel();
		panelCustomCube.setLayout(new GridLayout(4,1));
		panelXPos = addLabelTextPanelCustom("X-Pos: " , 0f);
		panelXPos.tf.addActionListener(new ListenForX());
		panelYPos = addLabelTextPanelCustom("Y-Pos: " , 0f);
		panelYPos.tf.addActionListener(new ListenForY());
		panelZPos = addLabelTextPanelCustom("Z-Pos: " , 0f);
		panelZPos.tf.addActionListener(new ListenForZ());
		buttonCustomCube = addButtonPanelCustomCube("Generate");
		buttonCustomCube.button.addActionListener(new ListenForCustomGenerate());
		tabbedPaneGenerate.addTab("Generate Cubes", panelGenerate);
		tabbedPaneGenerate.addTab("Custom Cube", panelCustomCube);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20,0,0,0); 
		frame.add(tabbedPaneGenerate, c);
		
		
		
		//startknop
		panelStart = new JPanel();
		buttonStart = addButtonPanelStart("start");
		buttonStart.button.addActionListener(new ListenForStartButton());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		frame.add(panelStart, c);
		
		update();
		
		/*
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
		
		frame.add(canvas, BorderLayout.CENTER);
		*/
		
		//frame.pack();
	
	
	}
	
	public void run() {
		
		frame.setVisible(true);
	}
	
	
	public LabelTextPanel addLabelTextPanelConfig(String text, Float value) {
		return new LabelTextPanel(text, value, panelConfig);
	}
	public LabelTextPanel addLabelTextPanelCustom(String text, Float value) {
		return new LabelTextPanel(text, value, panelCustomCube);
	}
	public ButtonPanel addButtonPanelStart(String text) {
		return new ButtonPanel(text, panelStart);
	}
	public ButtonPanel addButtonPanelGenerate(String text) {
		return new ButtonPanel(text, panelGenerate);
	}
	public ButtonPanel addButtonPanelCustomCube(String text) {
		return new ButtonPanel(text, panelCustomCube);
	}
	
	public void update() {
		positie.labelValue.setText("(" + round(dummyGame.drone.getPositionInWorld().x,2) +

				", " + round(dummyGame.drone.getPositionInWorld().y,2) + 
				", " + round(dummyGame.drone.getPositionInWorld().z,2) +
				")");
		hpr.labelValue.setText("(" + round(dummyGame.drone.getHeading(),2) +
				", " + round(dummyGame.drone.getPitch(),2) + 
				", " + round(dummyGame.drone.getRoll(),2) + 
				")");
		snelheid.labelValue.setText("(" + round(dummyGame.drone.getVelocityInWorld().x,2)+
				", " + round(dummyGame.drone.getVelocityInWorld().y,2)+ 
				", " + round(dummyGame.drone.getVelocityInWorld().z,2) + 
				")");
		versnelling.labelValue.setText("(" + round(dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).x,2) +
				", " + round(dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).y,2)+ 
				", " + round(dummyGame.drone.fysica.getAccelerationInWorld(dummyGame.drone).z,2) + 
				")");
		inclinatie.labelValue.setText("(" + round(dummyGame.drone.getLeftWingInclination(),2) + 
				", " + round(dummyGame.drone.getRightWingInclination(),2) + 
				", " + round(dummyGame.drone.getHorStabInclination(),2) + 
				", " + round(dummyGame.drone.getVerStabInclination(),2) +
				")");
		//aoa.labelValue.setText("(" + round(dummyGame.drone.getAOA(),2) + ")");		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// LISTENERS
	private class ListenForGenerate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.addGameItems(dummyGame.worldGenerator(5));
		}
	}
	
	private class ListenForX implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			xPos = Float.parseFloat(panelXPos.tf.getText());
		}
	}
	
	private class ListenForY implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			yPos = Float.parseFloat(panelYPos.tf.getText());
		}
	}
	
	private class ListenForZ implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			zPos = Float.parseFloat(panelZPos.tf.getText());
		}
	}
	
	private class ListenForCustomGenerate implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.addGameItemAtPos(xPos, yPos, zPos);
		}
	}
	
	public class ListenForFile implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		    //Handle open button action.
		    
			int returnVal = fc.showOpenDialog(panelGenerate);
	
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        File file = fc.getSelectedFile();
		        dummyGame.readCubesFromFile(file);
		        }
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
			dummyGame.drone.leftWing         = new Airfoil(0, dummyGame.drone.wingMass, false, 0.1f, new Vector3f(-input,0,0));
			dummyGame.drone.leftWing.setDrone(dummyGame.drone);
			dummyGame.drone.rightWing        = new Airfoil(0, dummyGame.drone.wingMass,   false, 0.1f, new Vector3f(input,0,0));
			dummyGame.drone.rightWing.setDrone(dummyGame.drone);
			dummyGame.drone.setInertiaMatrix();
		}
	}
	
	private class ListenForTailSize implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailsize.tf.getText());
			dummyGame.drone.tailSize = input;
			dummyGame.drone.horStabilization = new Airfoil(0, dummyGame.drone.tailMass/2, false, 0.05f, new Vector3f(0,0,input));
			dummyGame.drone.horStabilization.setDrone(dummyGame.drone);	
			dummyGame.drone.verStabilization = new Airfoil(0, dummyGame.drone.tailMass/2, true, 0.05f,  new Vector3f(0,0,input));
			dummyGame.drone.verStabilization.setDrone(dummyGame.drone);
			dummyGame.drone.setInertiaMatrix();
		}
	}
	
	private class ListenForEngineMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelenginemass.tf.getText());
			dummyGame.drone.engineMass = input;
			dummyGame.drone.engine = new Engine(0, input, new Vector3f(0,0,-1));
			dummyGame.drone.setInertiaMatrix();
		}
	}
	
	private class ListenForWingMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelwingmass.tf.getText());
			dummyGame.drone.wingMass= input;
			dummyGame.drone.leftWing         = new Airfoil(0, input,   false,  0.1f, new Vector3f(-dummyGame.drone.wingX,0,0));
			dummyGame.drone.leftWing.setDrone(dummyGame.drone);
			dummyGame.drone.rightWing        = new Airfoil(0, input,   false,  0.1f, new Vector3f(dummyGame.drone.wingX,0,0));
			dummyGame.drone.rightWing.setDrone(dummyGame.drone);
			dummyGame.drone.setInertiaMatrix();
		}
	}
	
	private class ListenForTailMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailmass.tf.getText());
			dummyGame.drone.tailMass = input;
			dummyGame.drone.horStabilization = new Airfoil(0, input/2, false,  0.05f, new Vector3f(0,0,dummyGame.drone.tailSize));
			dummyGame.drone.horStabilization.setDrone(dummyGame.drone);			
			dummyGame.drone.verStabilization = new Airfoil(0, input/2, true,  0.05f,  new Vector3f(0,0,dummyGame.drone.tailSize));
			dummyGame.drone.verStabilization.setDrone(dummyGame.drone);
			dummyGame.drone.setInertiaMatrix();
		}
	}
	
	private class ListenForMaxThrust implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelmaxthrust.tf.getText());
			dummyGame.drone.maxThrust = input;
			dummyGame.drone.engine.setMaxThrust(input);
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

	
    /**
     * Round to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
