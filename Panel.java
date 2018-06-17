import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Panel extends JPanel implements ActionListener {

	boolean start = false;
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Point> flowers = new ArrayList<Point>();

	int panel_x = 450, panel_y = 25, przesuniecie = 250;
	int lower_bound = -250, higher_bound = 250;
	long time;

	double x = 1;// random number

	int patch_size = higher_bound - lower_bound;
	int bee_per_flower = 3;
	int flower_nb = 3;
	int scouts = 3;
	double shrinking = 0.8;

	JTextField text_per_flower = new JTextField(String.valueOf(bee_per_flower));
	JTextField text_flowers = new JTextField(String.valueOf(flower_nb));
	JTextField text_scouts = new JTextField(String.valueOf(scouts));
	JTextField text_shrinking = new JTextField(String.valueOf(shrinking));

	public Panel() {

		int window_width = 1000;
		int window_height = 600;
		setPreferredSize(new Dimension(window_width, window_height));
		this.setLayout(null);

		JButton startButton = new JButton("START");
		JLabel label_per_flower = new JLabel("agents per flower");
		JLabel label_flowers = new JLabel("flowers");
		JLabel label_scouts = new JLabel("scouts");
		JLabel label_shrinking = new JLabel("shrinking rate");

		startButton.setBounds(100, window_height - 100, 100, 30);
		label_per_flower.setBounds(100, window_height - 200, 200, 30);
		label_flowers.setBounds(100, window_height - 260, 200, 30);
		label_scouts.setBounds(100, window_height - 320, 200, 30);
		label_shrinking.setBounds(100, window_height - 380, 200, 30);

		text_shrinking.setBounds(250, window_height - 380, 100, 30);
		text_scouts.setBounds(250, window_height - 320, 100, 30);
		text_flowers.setBounds(250, window_height - 260, 100, 30);
		text_per_flower.setBounds(250, window_height - 200, 100, 30);

		startButton.addActionListener(this);

		this.add(startButton);
		this.add(label_per_flower);
		this.add(label_flowers);
		this.add(label_scouts);
		this.add(label_shrinking);

		this.add(text_shrinking);
		this.add(text_scouts);
		this.add(text_flowers);
		this.add(text_per_flower);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		time = System.currentTimeMillis();
		shrinking = Double.parseDouble(text_shrinking.getText());
		scouts = Integer.parseInt(text_scouts.getText());
		bee_per_flower = Integer.parseInt(text_per_flower.getText());

		for (int i = 0; i < scouts + flower_nb * bee_per_flower; i++) {
			GenRandPoint();
		}

		for (int i = 0; i < flower_nb; i++) {
			flowers.add(new Point((int) points.get(i).getX(), (int) points.get(i).getY()));
		}

		start = !start;

		if (!start) {
			patch_size = higher_bound - lower_bound;
			flowers.clear();
			points.clear();
		}
	}

	public void GenRandPoint() {
		points.add(new Point((int) GenRandNumb(), (int) GenRandNumb()));
	}

	public double GenRandNumb() {
		return GenRandNumb(lower_bound, higher_bound);
	}

	public double GenRandNumb(int l, int h) {
		long m = 2147483647, a = 16808, c = 0;

		x = (a * x + c) % m;
		return (l + (x / (m - 1)) * (h - l));
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawRect(panel_x, panel_y, 500, 500);
		g2d.setColor(Color.RED);

		if (start) {

			//kwiaty
			for (int i = 0; i < flower_nb; i++) {
				for (int j = i * bee_per_flower + scouts; j < (i + 1) * bee_per_flower + scouts; j++) {

					int l = (int) (flowers.get(i).getX() - patch_size);
					int h = (int) (flowers.get(i).getX() + patch_size);
					points.get(j).x = (int) GenRandNumb(l, h);

					l = (int) (flowers.get(i).getY() - patch_size);
					h = (int) (flowers.get(i).getY() + patch_size);
					points.get(j).y = (int) GenRandNumb(l, h);

					if (function(points.get(j)) < function(flowers.get(i))) {
						flowers.get(i).x = (int) points.get(j).getX();
						flowers.get(i).y = (int) points.get(j).getY();
						patch_size *= shrinking;
					}
				}
			}

			// Sorting
			Collections.sort(flowers, new Comparator<Point>() {
				@Override
				public int compare(Point p2, Point p1) {
					return (int) (function(p2) - function(p1));
				}
			});

			for (int i = 0; i < scouts; i++) {
				points.get(i).x = (int) GenRandNumb();
				points.get(i).y = (int) GenRandNumb();

				if (function(points.get(i)) < function(flowers.get(flower_nb - 1))) {
					flowers.get(flower_nb - 1).x = (int) points.get(i).getX();
					flowers.get(flower_nb - 1).y = (int) points.get(i).getY();
				}
			}

			for (Point point : points) {
				g2d.fillOval(panel_x + point.x + przesuniecie, panel_y + point.y + przesuniecie, 4, 4);
			}
		}

		repaint();
		try {
			TimeUnit.MILLISECONDS.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis();
	}

	public double function(Point var) {
		return Example2(var);
	}

	public double Example(Point var) {
		return Math.sin(var.getX()) + Math.pow(var.getX(), 2) - Math.pow(var.getY(), 2);// sin(x)+x^2-y^2
	}

	public double Example2(Point var) {
		return Math.pow(var.getX(), 2) + Math.pow(var.getY(), 2)
				+ 25 * (Math.pow(Math.sin(var.getX()), 2) + Math.pow(Math.sin(var.getY()), 2));
	}
	/*
	 * public double Auckley(Point var) { double sum1 = 0.0; double sum2 = 0.0;
	 * 
	 * for (int i = 0 ; i < x.length ; i ++) { sum1 += Math.pow(x[i], 2); sum2 +=
	 * (Math.cos(2*Math.PI*x[i])); }
	 * 
	 * return -20.0*Math.exp(-0.2*Math.sqrt(sum1 / ((double )x.length))) + 20 -
	 * Math.exp(sum2 /((double )x.length)) + Math.exp(1.0); }
	 */
}
