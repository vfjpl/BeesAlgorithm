import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Frame extends JFrame {

	public static void main(String[] args) {

		new Frame();
	}

	public Frame() {

		super("Differential Evolution");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int window_width = 1000;
		int window_height = 600;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - window_width) / 2, (screenSize.height - window_height) / 2);

		add(new Panel());
		pack();

		setVisible(true);
	}
}
