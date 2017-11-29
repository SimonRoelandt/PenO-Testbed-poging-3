package GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;


public class LabelPanel {
	JPanel panel;
	JLabel labelName, labelValue;
	JPanel goalPanel;

	public LabelPanel(String text, JPanel goalPanel) {
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));
		labelName = new JLabel(text + ": ");
		labelValue = new JLabel("");
		panel.add(labelName);
		panel.add(labelValue);
		goalPanel.add(panel);
	}
	
	
}
