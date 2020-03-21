import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * The GUI for this program.  Highest level swing component.
 * 
 * @author Samuel Lieberman
 *
 */
public class FractalGUI extends JFrame{
	private static final long serialVersionUID = 7296079999810708782L;
	
	private AbstractFractalViewer fractalViewer;
	/**
	 * This is a list of instances of each fractal. Any new fractal should be added
	 * to this list to automatically be included in the program.
	 */
	private static final RecursiveFractal[] FRACTALS = {
			new MandelbrotSet(),
			new TriangleFractal(),
			new JuliaSet_phi(),
			new JuliaSet_687_312i(),
			new JuliaCauliflower(),
			new JuliaSet_neg1(),
			new TestFractal(),
	};
	private static final int START_FRACTAL = 0;
	
	private FractalButton[] fractalButtons;
	
	/**
	 * constructs and sets up the GUI
	 */
	public FractalGUI() {
		super("Fractal Drawer");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		initGUI();
		
		pack();
		setVisible(true);
		
		fractalButtons[START_FRACTAL].select();
	}
	/**
	 * Initializes the actual components that make up this GUI
	 */
	private void initGUI() {
		fractalViewer = new FractalViewer();
		add(fractalViewer, BorderLayout.CENTER);
		
		JScrollPane choicePane = new JScrollPane();
		add(choicePane, BorderLayout.WEST);
		
		choicePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		choicePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JPanel choiceButtonsPanel = new JPanel();
		choicePane.setViewportView(choiceButtonsPanel);
		
		choiceButtonsPanel.setLayout(new BoxLayout(choiceButtonsPanel, BoxLayout.Y_AXIS));
		
		fractalButtons = new FractalButton[FRACTALS.length];
		for (int i = 0; i < FRACTALS.length; i++) {
			FractalButton fractalButton = new FractalButton(FRACTALS[i], fractalViewer, this);
			choiceButtonsPanel.add(fractalButton);
			
			fractalButtons[i] = fractalButton;
		}
	}
	
	/**
	 * Updates each button's enabled visuals. This method is called by any button
	 * after being pressed.
	 */
	public void updateButtons() {
		for (FractalButton button:fractalButtons) {
			button.updateEnabled();
		}
	}
}
