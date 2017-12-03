package GUI;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;


public class ButtonPanel {
	JPanel panel;
	JButton button;
	JFrame frame;

	public ButtonPanel(String text, JPanel panelStart) {
		panel = new JPanel();
		button = new JButton(text);
		panel.add(button);
		panelStart.add(panel);
	}	
}
