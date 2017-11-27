package GUI;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;


public class LabelPanel {
	JPanel panel;
	JLabel label;
	JFrame frame;

	public LabelPanel(String text, JFrame frame) {
		panel = new JPanel();
		label = new JLabel("positie: ");
		panel.add(label);
		frame.add(panel);
	}
	
	
}
