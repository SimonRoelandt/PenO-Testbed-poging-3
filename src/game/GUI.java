package game;



import javax.swing.JFrame;

public class GUI {
	private JFrame frame;
	
	public GUI() {
		
	}
	
	void init() {
		JFrame frame = new JFrame("hello");
		this.frame = frame;
	}
	
	void run() {
		frame.setSize(1500, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	
}
