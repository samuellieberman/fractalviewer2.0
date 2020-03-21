import javax.swing.JPanel;

/**
 * Displays a fractal on it's canvas. Has a runtime adjustable width and height.
 * Pixel size can only be selected at compile time/when programming. Includes an
 * indicator showing where the next potential zoom would be. Is continuously
 * doing all the calculations for which points are part of the fractal or not.
 * 
 * Is extended by FractalViewer and FractalViewer2. This class exists to easily
 * be able to swap between the two.
 * 
 * @author Samuel Lieberman
 *
 */
public abstract class AbstractFractalViewer extends JPanel {
	private static final long serialVersionUID = -4521623044448557872L;

	public abstract void start(RecursiveFractal fractal);
}
