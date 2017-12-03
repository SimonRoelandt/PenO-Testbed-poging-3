package GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;


public class LabelTextPanel {
	JPanel panel;
	JLabel label;
	JTextField tf;
	JPanel panelGoal;

	public LabelTextPanel(String text, float value, JPanel panelGoal) {
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		label = new JLabel(text, SwingConstants.LEFT);
		tf = new JTextField(Float.toString(value), 15);
		panel.add(label);
		panel.add(tf);
		panelGoal.add(panel);
	}
	
	
}
