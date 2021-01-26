package odds.portal.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import odds.portal.OddsPortal;
import odds.portal.Reservation;

public class Gui {
	JButton boutonPause;
	OddsPortal oddsPortal;
	Frame frame;
	private JLabel label2;

	public void setNombre(String nombre) {
		label2.setText(nombre);
	}

	protected void addAButton(String text, Container container) {
		boutonPause = new JButton(text);
		boutonPause.setPreferredSize(new Dimension(100, 100));
		// boutonPause.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(boutonPause);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		frame = new Frame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		oddsPortal = new OddsPortal(this);
		WindowListener windowListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				oddsPortal.interrupt();
				System.exit(0);
			}
		};
		frame.addWindowListener(windowListener);
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		double width = winSize.getWidth();
		double height = winSize.getHeight();

		//frame.setPreferredSize(new Dimension((int) width / 2, (int) height));
		frame.setLocation(0, 0);
		// Display the window.
		frame.pack();
		frame.setName(OddsPortal.NOM_FENETRE);
		frame.setGui(this);
		frame.setVisible(true);

	}

	public Frame getFrame() {
		return frame;
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this booking's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui();
				gui.createAndShowGUI();
			}
		});
	}



	public OddsPortal getOddsPortal() {
		return oddsPortal;
	}

	public void setBooking(OddsPortal booking) {
		this.oddsPortal = booking;
	}

}