package GUI;

import java.awt.Dimension;
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
	JFrame frame;

	public LabelTextPanel(String text, JFrame frame) {
		panel = new JPanel();
		label = new JLabel(text);
		tf = new JTextField(15);
		panel.add(label);
		panel.add(tf);
		frame.add(panel);
	}
	
	
}
