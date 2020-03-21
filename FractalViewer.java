import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Displays a fractal on it's canvas. Has a runtime adjustable width and height.
 * Pixel size can only be selected at compile time/when programming. Includes an
 * indicator showing where the next potential zoom would be. Is continuously
 * doing all the calculations for which points are part of the fractal or not.
 * 
 * Different from FractalViewer2 in that it supports fractals like the
 * Mandelbrot Set, which requires doing different sets of calculations depending
 * on your starting position.
 * 
 * @author Samuel Lieberman
 *
 */
public class FractalViewer extends AbstractFractalViewer{
	private static final long serialVersionUID = 160672894922481585L;
	
	private static final int START_WIDTH = 400;
	private static final int START_HEIGHT = 400;
	private static final int PIXEL_SIZE = 2;
	
	private static final double ZOOM_FACTOR = 2;
	private static final double INDICATOR_THICKNESS = 5;
	private static final Color INDICATOR_COLOR = new Color(255, 255, 255);
	
	private static final Color CONVERGE_COLOR = new Color(0, 0, 0);
	//private static final Color ERROR_COLOR = new Color(255, 0, 0);
	private static final Color[] COLOR_PATTERN = {
			new Color(255, 0, 0),
			new Color(255, 255, 0),
			new Color(0, 255, 127),
			new Color(0, 127, 127),
			new Color(0, 0, 255),
			new Color(127, 0, 127),
	};
	private static final int ITERATIONS_PER_COLOR = 2;
	
	private RecursiveFractal fractal;
	
	private double fracDiameter;
	private double fracOverComp;
	//private double compOverFrac;
	private double fracWidth;
	private double fracHeight;
	private Complex fracCenter;
	private Complex fracTopLeft;
	//private Complex fracBottomRight;
	
	private Thread drawThread = null;
	private Repainter repainter = null;
	private Complex[][] pixelPositions;
	private Complex[][] pixelValues;
	private boolean[][] pixelConverges;
	private Color[][] pixelColors;
	
	private boolean mouseIsIn;
	private int mouseX;
	private int mouseY;
	
	private int iterations;
	private Color currentColor;
	
	public FractalViewer() {
		mouseIsIn = false;
		
		addMouseMotionListener(new MotionDetector());
		addMouseListener(new ZoomDetector());
		addComponentListener(new ResizeDetector());
		
		setPreferredSize(new Dimension(START_WIDTH, START_HEIGHT));
	}
	public void start(RecursiveFractal fractal) {
		this.fractal = fractal;
		
		initForPosition(fractal.getInitialScreenDiameter(), fractal.getInitialScreenCenter());
	}
	private void initForPosition(double fracDiameter, Complex fracCenter) {
		if (drawThread != null && repainter != null) {
			repainter.end();
			try {
				drawThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		setPosition(fracDiameter, fracCenter);
		
		iterations = 0;
		initPixelValues();
		
		repainter = new Repainter();
		drawThread = new Thread(repainter);
		drawThread.start();
	}
	
	private void setPosition(double fracDiameter, Complex fracCenter) {
		int compWidth = getWidth();
		int compHeight = getHeight();
		//int compCenterX = compWidth/2;//rounded down to the nearest pixel
		//int compCenterY = compHeight/2;//rounded down to the nearest pixel
		int compDiameter;
		
		if (compWidth < compHeight) {
			compDiameter = compWidth;
		}else {
			compDiameter = compHeight;
		}
		
		this.fracDiameter = fracDiameter;
		fracOverComp = fracDiameter/compDiameter;
		//compOverFrac = compDiameter/fracDiameter;
		fracWidth = compWidth*fracOverComp;
		fracHeight = compHeight*fracOverComp;
		this.fracCenter = fracCenter;
		fracTopLeft = new Complex(fracCenter.re() - fracWidth/2, fracCenter.im() - fracHeight/2, ImMath.coordinateSystem.CARTISAN);
		//fracBottomRight = new Complex(fracCenter.re() + fracWidth/2, fracCenter.im() + fracHeight/2, ImMath.coordinateSystem.CARTISAN);
	}
	private Complex compPosToFracPos(int compX, int compY) {
		return new Complex(fracTopLeft.re() + compX*fracOverComp, fracTopLeft.im() + compY*fracOverComp, ImMath.coordinateSystem.CARTISAN);
	}
	
	private void initPixelValues() {
		int pixelsWidth = (int) Math.ceil(getWidth()/(double)(PIXEL_SIZE));
		int pixelsHeight = (int) Math.ceil(getHeight()/(double)(PIXEL_SIZE));
		
		pixelPositions = new Complex[pixelsWidth][pixelsHeight];
		pixelValues = new Complex[pixelsWidth][pixelsHeight];
		pixelConverges = new boolean[pixelsWidth][pixelsHeight];
		pixelColors = new Color[pixelsWidth][pixelsHeight];
		for (int pixX = 0; pixX < pixelsWidth; pixX++) {
			for (int pixY = 0; pixY < pixelsHeight; pixY++) {
				int compX = pixX*PIXEL_SIZE;
				int compY = pixY*PIXEL_SIZE;
				
				Complex positionValue = new Complex(fracTopLeft.re() + compX*fracOverComp, fracTopLeft.im() + compY*fracOverComp);
				pixelPositions[pixX][pixY] = positionValue;
				pixelValues[pixX][pixY] = fractal.start(positionValue);
				pixelConverges[pixX][pixY] = true;
				pixelColors[pixX][pixY] = CONVERGE_COLOR;
			}
		}
	}
	private void incrementPixelValues() {
		for (int x = 0; x < pixelValues.length; x++) {
			for (int y = 0; y < pixelValues[x].length; y++) {
				if (pixelConverges[x][y]) {
					Complex oldValue = pixelValues[x][y];
					Complex newValue = fractal.step(oldValue, pixelPositions[x][y]);
					pixelValues[x][y] = newValue;
					if (fractal.diverges(newValue, iterations)) {
						pixelConverges[x][y] = false;
						pixelColors[x][y] = currentColor;
					}
				}
			}
		}
	}
	
	private void incrementColor() {
		//cycles through each of the colors switching every 10 iterations
		currentColor = COLOR_PATTERN[(iterations/ITERATIONS_PER_COLOR)%COLOR_PATTERN.length];
	}
	
	protected void incrementIterations() {
		iterations++;
		incrementColor();
		
		incrementPixelValues();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		for (int pixX = 0; pixX < pixelColors.length; pixX++) {
			for (int pixY = 0; pixY < pixelColors[pixX].length; pixY++) {
				int compX = pixX*PIXEL_SIZE;
				int compY = pixY*PIXEL_SIZE;
				
				g.setColor(pixelColors[pixX][pixY]);
				
				g.fillRect(compX, compY, PIXEL_SIZE, PIXEL_SIZE);//draws a point
			}
		}
		
		if (mouseIsIn) {
			int compWidth = getWidth();
			int compHeight = getHeight();
			int indicatorWidth = (int) (compWidth/ZOOM_FACTOR);
			int indicatorHeight = (int) (compHeight/ZOOM_FACTOR);
			
			g.setColor(INDICATOR_COLOR);
			for (int i = 0; i < INDICATOR_THICKNESS; i++) {
				g.drawRect(mouseX-indicatorWidth/2-i, mouseY-indicatorHeight/2-i, indicatorWidth+2*i, indicatorHeight+2*i);
			}
		}
	}
	
	private class MotionDetector implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent arg0) {}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			mouseX = arg0.getX();
			mouseY = arg0.getY();
			
			repaint();
		}
	}
	private class ZoomDetector implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			Complex newCenter = compPosToFracPos(mouseX, mouseY);
			double newFracDiameter;
			if (arg0.isControlDown()) {
				newFracDiameter = fracDiameter*ZOOM_FACTOR;
			}else {
				newFracDiameter = fracDiameter/ZOOM_FACTOR;
			}
			
			initForPosition(newFracDiameter, newCenter);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			mouseIsIn = true;
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			mouseIsIn = false;
			
			repaint();
		}

		@Override public void mousePressed(MouseEvent arg0) {}
		@Override public void mouseReleased(MouseEvent arg0) {}
	}
	private class ResizeDetector implements ComponentListener {
		@Override public void componentHidden(ComponentEvent arg0) {}
		@Override public void componentMoved(ComponentEvent arg0) {}
		
		@Override
		public void componentResized(ComponentEvent arg0) {
			if (fracCenter != null) {
				initForPosition(fracDiameter, fracCenter);
			}
		}
		
		@Override public void componentShown(ComponentEvent arg0) {}
	}
	private class Repainter implements Runnable{
		private boolean end = false;
		
		public void end() {
			end = true;
		}
		
		@Override
		public void run() {
			while (!end) {
				incrementIterations();
				repaint();
			}
		}
	}
}
