package GUI;

import game.DummyGame;

public class GuiTest {
	public static void main(String[] args) {
		GUI gui = new GUI(new DummyGame());
		gui.init();
		gui.run();
	}
}
