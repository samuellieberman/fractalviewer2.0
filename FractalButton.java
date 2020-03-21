import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * Updates the GUI to display the new fractal represented by the private
 * RecursiveFractal instance variable.
 * 
 * @author Samuel Lieberman
 *
 */
public class FractalButton extends JButton {
	private static final long serialVersionUID = -2547387484104386401L;
	
	private static RecursiveFractal currentFractal;
	private RecursiveFractal fractal;
	private AbstractFractalViewer viewer;
	private FractalGUI parent;
	
	public FractalButton(RecursiveFractal fractal, AbstractFractalViewer viewer, FractalGUI parent) {
		this.fractal = fractal;
		this.viewer = viewer;
		this.parent = parent;
		
		setText(fractal.getName());
		setToolTipText(fractal.getFormula());
		
		addActionListener(new ClickListener());
	}
	/**
	 * updates the GUI to display the fratal represented by the private RecursiveFractal instance variable
	 */
	public void select() {
		currentFractal = fractal;
		parent.updateButtons();
		viewer.start(fractal);
	}
	
	/**
	 * This button is only enabled depending on if it's currentFractal is being used.
	 */
	public void updateEnabled() {
		setEnabled(!fractal.getClass().equals(currentFractal.getClass()));
	}
	
	/**
	 * @return the fractal that this button displays
	 */
	public RecursiveFractal getFractal() {
		return fractal;
	}
	/**
	 * @return the JPanel displaying the fractal
	 */
	public AbstractFractalViewer getViewer() {
		return viewer;
	}
	
	public class ClickListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			select();
		}
	}
}
