package GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.*;

import org.lwjgl.util.vector.Vector3f;

import drone.Airfoil;
import drone.Drone;
import drone.Engine;
import engine.GameItem;

import javax.swing.*;

import game.DronesController;
import game.DummyGame;

public class GUI {
	DummyGame dummyGame;
	private JFrame frame;
	JButton buttonPlane, buttonChase, buttonSide, buttonFree;
	int buttonClicked;				
	JPanel panelViews, panelConfig, panelValues, panelStart, panelGenerate, panelCustomCube, panelRemove, panelChooseDrone,
			panelAddPackage;
	JLabel label1, label2;
	JTextField textfield1, textfield2;
	LabelTextPanel panelWingX, paneltailsize, panelenginemass, panelwingmass, paneltailmass, panelmaxthrust, panelmaxaoa, panelXPos, panelYPos, panelZPos,
					panelFromAirport, panelToAirport;
	GLPanel glpanel;
	ButtonPanel buttonStart, buttonGenerate, buttonChooseFile, buttonCustomCube, buttonAddPackage;
	LabelPanel positie, hpr, snelheid, versnelling, inclinatie, aoa, thrust, force, resulmoment;
	JFileChooser fc;
	JTabbedPane tabbedPaneGenerate;
	JList<GameItem> list;
	DefaultListModel<GameItem> listModel;
	List<JButton> buttonDrones = new ArrayList<JButton>();
	
	private ArrayList<Drone> drones;
	
    private DronesController dronesController;
    
    private Drone currentDrone;
    
    private int droneAmount;
    

	
	float xPos, yPos, zPos;
	
	int fromAirport, toAirport;
	
	public GUI(DummyGame dummyGame) {
		this.dummyGame = dummyGame;
		this.dronesController = this.dummyGame.droneController;
		this.drones = this.dronesController.getDrones();
		currentDrone = drones.get(0);
		droneAmount = drones.size();

	}
	
	public void init() {	
		
		
		
		frame = new JFrame("Testbed Team Wit");
		frame.setSize(400, 1200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(1300, 20);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		
		
		
		
		panelChooseDrone = new JPanel();
		panelChooseDrone.setLayout(new GridLayout(1,droneAmount));
		

		for (Drone drone : drones) {
			int count = drones.indexOf(drone);
			JButton button = new JButton("Drone " + (count +1));
			button.setFocusPainted(false);
			buttonDrones.add(button);
			button.addActionListener(new ListenForDrone(count));
			panelChooseDrone.add(button);
		}

		Border droneBorder = BorderFactory.createTitledBorder("Drones");
		panelChooseDrone.setBorder(droneBorder);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		
		frame.add(panelChooseDrone, c);
		
		
		panelValues = new JPanel();
		panelValues.setLayout(new GridLayout(9,1));
		Border valuesBorder = BorderFactory.createTitledBorder("Values");
		panelValues.setBorder(valuesBorder);
		positie = new LabelPanel("Position", panelValues);
		hpr = new LabelPanel("Head, Pitch, Roll", panelValues);
		snelheid =  new LabelPanel("Velocity", panelValues);
		versnelling = new LabelPanel("Acceleration", panelValues);
		inclinatie = new LabelPanel("Inclinations left, right, hor, ver", panelValues);
		thrust = new LabelPanel("Thrust", panelValues);
		force = new LabelPanel("force", panelValues);
		aoa = new LabelPanel("Angle of Attack", panelValues);
		resulmoment = new LabelPanel("Resulting Moment", panelValues);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelValues, c);
		
		
		//panelen om in te vullen: 
		panelConfig = new JPanel();
		panelConfig.setLayout(new GridLayout(7,1));
		Border configBorder = BorderFactory.createTitledBorder("Configuration");
		panelConfig.setBorder(configBorder);
		

		//TEMP GUI FIX
		
		panelWingX = addLabelTextPanelConfig("WingX", currentDrone.wingX);
		panelWingX.tf.addActionListener(new ListenForWingX());
		paneltailsize = addLabelTextPanelConfig("Tail Size", currentDrone.tailSize);
		paneltailsize.tf.addActionListener(new ListenForTailSize());
		panelenginemass = addLabelTextPanelConfig("Engine Mass", currentDrone.engineMass);
		panelenginemass.tf.addActionListener(new ListenForEngineMass());
		panelwingmass = addLabelTextPanelConfig("Wing Mass", currentDrone.wingMass);
		panelwingmass.tf.addActionListener(new ListenForWingMass());
		paneltailmass = addLabelTextPanelConfig("Tail Mass", currentDrone.tailMass);
		paneltailmass.tf.addActionListener(new ListenForTailMass());
		panelmaxthrust = addLabelTextPanelConfig("Max Thrust", currentDrone.maxThrust);
		panelmaxthrust.tf.addActionListener(new ListenForMaxThrust());
		panelmaxaoa = addLabelTextPanelConfig("Max AOA", currentDrone.maxAOA);
		panelmaxaoa.tf.addActionListener(new ListenForMaxAOA());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelConfig, c);
		
		
		
		
		//panel met view-buttons
		buttonPlane = new JButton("Plane");
		buttonPlane.setFocusPainted(false);
		buttonPlane.addActionListener(new ListenForPlaneButton());
		buttonChase = new JButton("Chase");
		buttonChase.setFocusPainted(false);
		buttonChase.addActionListener(new ListenForChaseButton());
		buttonSide = new JButton("Ortho");
		buttonSide.setFocusPainted(false);
		buttonSide.addActionListener(new ListenForSideButton());
		buttonFree = new JButton("Custom");
		buttonFree.setFocusPainted(false);
		buttonFree.addActionListener(new ListenForFreeButton());
		
		panelViews = new JPanel();
		panelViews.setLayout(new GridLayout(1,4));
		panelViews.add(buttonFree);
		panelViews.add(buttonChase);
		panelViews.add(buttonPlane);
		panelViews.add(buttonSide);
		
		Border viewBorder = BorderFactory.createTitledBorder("Views");
		panelViews.setBorder(viewBorder);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelViews, c);
		// tot hier
		
		////////////////////// IN COMMENT VOOR DEMO
		/*
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
		
		panelRemove = new JPanel();
		panelRemove.setLayout(new GridLayout(3,1));
		listModel = new DefaultListModel<GameItem>();
		
		list = new JList<GameItem>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		//JScrollPane listScroller = new JScrollPane(list);
		//listScroller.setPreferredSize(new Dimension(250, 80));
		
		panelRemove.add(list);
		
		tabbedPaneGenerate.addTab("Generate Cubes", panelGenerate);
		tabbedPaneGenerate.addTab("Custom Cube", panelCustomCube);
		//tabbedPaneGenerate.addTab("Remove Cubes", panelRemove);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(20,0,0,0); 
		frame.add(tabbedPaneGenerate, c);
		*/
		
		
		////////////////////////// TOT HIER
		////////////////////////// als uncomment: c.gridy telkens +1 voor hieronder
		
		
		//PACKAGE
		panelAddPackage = new JPanel();
		panelAddPackage.setLayout(new GridLayout(4,1));
		panelFromAirport = addLabelTextPanelAddPackage("From Airport: " , 0);
		panelFromAirport.tf.addActionListener(new ListenForFromAirport());
		panelToAirport = addLabelTextPanelAddPackage("To Airport: " , 0);
		panelToAirport.tf.addActionListener(new ListenForToAirport());
		

		buttonAddPackage = addButtonPanelAddPackage("Add Package");
		buttonAddPackage.button.setFocusPainted(false);
		buttonAddPackage.button.addActionListener(new ListenForAddPackage());
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(20,0,0,0); 
		frame.add(panelAddPackage, c);
		
		//startknop
		panelStart = new JPanel();
		buttonStart = addButtonPanelStart("start");
		buttonStart.button.addActionListener(new ListenForStartButton());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		frame.add(panelStart, c);
		
		frame.pack();
		
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
	public ButtonPanel addButtonPanelAddPackage(String text) {
		return new ButtonPanel(text, panelAddPackage);
	}
	public LabelTextPanel addLabelTextPanelAddPackage(String text, int value) {
		return new LabelTextPanel(text, value, panelAddPackage);
	}
	
	public void update() {
		
		positie.labelValue.setText("(" + round(currentDrone.getState().getPosition().x,2) +

				", " + round(currentDrone.getState().getPosition().y,2) + 
				", " + round(currentDrone.getState().getPosition().z,2) +
				")");
		hpr.labelValue.setText("(" + round(currentDrone.getHeading(),2) +
				", " + round(currentDrone.getPitch(),2) + 
				", " + round(currentDrone.getRoll(),2) + 
				")");
		snelheid.labelValue.setText("(" + round(currentDrone.getState().getVelocity().x,2)+
				", " + round(currentDrone.getState().getVelocity().y,2)+ 
				", " + round(currentDrone.getState().getVelocity().z,2) + 
				")");
		versnelling.labelValue.setText("(" + round(currentDrone.fysica.accelerationInWorld.x,2) +
				", " + round(currentDrone.fysica.accelerationInWorld.y,2)+ 
				", " + round(currentDrone.fysica.accelerationInWorld.z,2) + 
				")");
		inclinatie.labelValue.setText("(" + round(currentDrone.getLeftWingInclination(),2) + 
				", " + round(currentDrone.getRightWingInclination(),2) + 
				", " + round(currentDrone.getHorStabInclination(),2) + 
				", " + round(currentDrone.getVerStabInclination(),2) +
				")");
		
		thrust.labelValue.setText("(" + round(currentDrone.engine.getThrustScalar(),5) + ")");

		force.labelValue.setText("(" + round(currentDrone.fysica.totalForceOnDroneInWorld.getX(),0) +
				", " + round(currentDrone.fysica.totalForceOnDroneInWorld.getY(),0) +
				", " + round(currentDrone.fysica.totalForceOnDroneInWorld.getZ(),0) +
				")");
		
		aoa.labelValue.setText("(" + currentDrone.leftWing.getAngleOfAttack() + ")");
		//aoa.labelValue.setText("(" + round(currentDrone.getAOA(),2) + ")");
		
		resulmoment.labelValue.setText("(" + round(currentDrone.getState().getMoment().getX(), 0) +
				", " + round(currentDrone.getState().getMoment().getY(),0) +
				", " + round(currentDrone.getState().getMoment().getZ(),0) +
				")");
	}
	
	
	
	

	// LISTENERS
	private class ListenForDrone implements ActionListener {
		int number;
		public ListenForDrone(int number) {
			this.number = number;
		}
		public void actionPerformed(ActionEvent e) {
			currentDrone = drones.get(number);
			dummyGame.currentDroneId = number;
			for (Component viewbutton : panelChooseDrone.getComponents()) {
				viewbutton.setBackground(null);
			}
			buttonDrones.get(number).setBackground(Color.RED);
		}
	}
	
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
			for (Component viewbutton : panelViews.getComponents()) {
				viewbutton.setBackground(null);
			}
			buttonPlane.setBackground(Color.RED);
		}
	}
	
	private class ListenForChaseButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "chase";
			for (Component viewbutton : panelViews.getComponents()) {
				viewbutton.setBackground(null);
			}
			buttonChase.setBackground(Color.RED);
		}
	}
	
	private class ListenForSideButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "side";
			for (Component viewbutton : panelViews.getComponents()) {
				viewbutton.setBackground(null);
			}
			buttonSide.setBackground(Color.RED);
		}
	}
	
	private class ListenForFreeButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.renderer.view =  "free";
			for (Component viewbutton : panelViews.getComponents()) {
				viewbutton.setBackground(null);
			}
			buttonFree.setBackground(Color.RED);
		}
	}
	
	private class ListenForStartButton implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dummyGame.startSimulation = true;
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("============================= START SIMULATION ===============");
		}
	}
	
	private class ListenForWingX implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelWingX.tf.getText());
			currentDrone.wingX = input;
			currentDrone.leftWing         = new Airfoil(0, currentDrone.wingMass, false, 0.1f, new Vector3f(-input,0,0),currentDrone);
			currentDrone.leftWing.setDrone(currentDrone);
			currentDrone.rightWing        = new Airfoil(0, currentDrone.wingMass,   false, 0.1f, new Vector3f(input,0,0),currentDrone);
			currentDrone.rightWing.setDrone(currentDrone);
			currentDrone.setInertiaMatrix();
		}
	}
	
	private class ListenForTailSize implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailsize.tf.getText());
			currentDrone.tailSize = input;
			currentDrone.horStabilization = new Airfoil(0, currentDrone.tailMass/2, false, 0.05f, new Vector3f(0,0,input),currentDrone);
			currentDrone.horStabilization.setDrone(currentDrone);	
			currentDrone.verStabilization = new Airfoil(0, currentDrone.tailMass/2, true, 0.05f,  new Vector3f(0,0,input),currentDrone);
			currentDrone.verStabilization.setDrone(currentDrone);
			currentDrone.setInertiaMatrix();
		}
	}
	
	private class ListenForEngineMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelenginemass.tf.getText());
			currentDrone.engineMass = input;
			currentDrone.engine = new Engine(0, input, new Vector3f(0,0,-1),currentDrone,currentDrone.getMaxThrust());
			currentDrone.setInertiaMatrix();
		}
	}
	
	private class ListenForWingMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelwingmass.tf.getText());
			currentDrone.wingMass= input;
			currentDrone.leftWing         = new Airfoil(0, input,   false,  0.1f, new Vector3f(-currentDrone.wingX,0,0),currentDrone);
			currentDrone.leftWing.setDrone(currentDrone);
			currentDrone.rightWing        = new Airfoil(0, input,   false,  0.1f, new Vector3f(currentDrone.wingX,0,0),currentDrone);
			currentDrone.rightWing.setDrone(currentDrone);
			currentDrone.setInertiaMatrix();
		}
	}
	
	private class ListenForTailMass implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(paneltailmass.tf.getText());
			currentDrone.tailMass = input;
			currentDrone.horStabilization = new Airfoil(0, input/2, false,  0.05f, new Vector3f(0,0,currentDrone.tailSize),currentDrone);
			currentDrone.horStabilization.setDrone(currentDrone);			
			currentDrone.verStabilization = new Airfoil(0, input/2, true,  0.05f,  new Vector3f(0,0,currentDrone.tailSize),currentDrone);
			currentDrone.verStabilization.setDrone(currentDrone);
			currentDrone.setInertiaMatrix();
		}
	}
	
	private class ListenForMaxThrust implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelmaxthrust.tf.getText());
			currentDrone.maxThrust = input;
			currentDrone.engine.setMaxThrust(input);
		}
	}
	
	private class ListenForMaxAOA implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float input = Float.parseFloat(panelmaxaoa.tf.getText());
			currentDrone.maxAOA = input;
		}
	}
	
	private class ListenForFromAirport implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fromAirport = Integer.parseInt(panelFromAirport.tf.getText());
		}
	}
	
	private class ListenForToAirport implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			toAirport = Integer.parseInt(panelToAirport.tf.getText());
		}
	}
	
	private class ListenForAddPackage implements ActionListener {
		public void actionPerformed(ActionEvent e) {	
			dummyGame.addPackage(fromAirport, toAirport);
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
