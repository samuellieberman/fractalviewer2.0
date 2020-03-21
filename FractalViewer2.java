import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
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
 * Different from FractalViewer in that it doesn't support fractals like the
 * Mandelbrot Set, which requires doing different sets of calculations depending
 * on your starting position. Because of this, it was able to be optimized for
 * these simpler fractals.  Works a lot faster than FractalViewer
 * 
 * @author Samuel Lieberman
 *
 */
public class FractalViewer2 extends AbstractFractalViewer{
	private static final long serialVersionUID = 4174601832444627962L;
	
	private static final int START_WIDTH = 800;
	private static final int START_HEIGHT = 800;
	private static final int PIXEL_SIZE = 1;
	
	private static final double ZOOM_FACTOR = 2;
	private static final double INDICATOR_THICKNESS = 5;
	private static final Color INDICATOR_COLOR = new Color(255, 255, 255);
	
	private static final Color UNCERTAIN_COLOR = new Color(50, 50, 50);
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
	
	private int compWidth;
	private int compHeight;
	private int compDiameter;
	private int pixWidth;
	private int pixHeight;
	private double fracDiameter;
	private double fracOverComp;
	private double compOverFrac;
	private double fracWidth;
	private double fracHeight;
	private Complex fracCenter;
	private Complex fracTopLeft;
	//private Complex fracBottomRight;
	
	private Thread drawThread = null;
	private Repainter repainter = null;
	private Pixel[][] pixels;
	
	private boolean mouseIsIn;
	private int mouseX;
	private int mouseY;
	
	private int iterations;
	//private Color currentColor;
	
	private boolean started;
	
	public FractalViewer2() {
		mouseIsIn = false;
		
		started = false;
		
		addMouseMotionListener(new MotionDetector());
		addMouseListener(new ZoomDetector());
		addComponentListener(new ResizeDetector());
		
		setPreferredSize(new Dimension(START_WIDTH, START_HEIGHT));
	}
	public void start(RecursiveFractal fractal) {
		this.fractal = fractal;
		
		initForPosition(fractal.getInitialScreenDiameter(), fractal.getInitialScreenCenter());
		
		started = true;
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
		compWidth = getWidth();
		compHeight = getHeight();
		//int compCenterX = compWidth/2;//rounded down to the nearest pixel
		//int compCenterY = compHeight/2;//rounded down to the nearest pixel
		
		pixWidth = (int) Math.ceil((double) (compWidth)/PIXEL_SIZE);
		pixHeight = (int) Math.ceil((double) (compHeight)/PIXEL_SIZE);
		
		if (compWidth < compHeight) {
			compDiameter = compWidth;
		}else {
			compDiameter = compHeight;
		}
		
		this.fracDiameter = fracDiameter;
		fracOverComp = fracDiameter/compDiameter;
		compOverFrac = compDiameter/fracDiameter;
		fracWidth = compWidth*fracOverComp;
		fracHeight = compHeight*fracOverComp;
		this.fracCenter = fracCenter;
		fracTopLeft = new Complex(fracCenter.re() - fracWidth/2, fracCenter.im() - fracHeight/2, ImMath.coordinateSystem.CARTISAN);
		//fracBottomRight = new Complex(fracCenter.re() + fracWidth/2, fracCenter.im() + fracHeight/2, ImMath.coordinateSystem.CARTISAN);
	}
	
	private Point pixPosToCompPos(int pixX, int pixY) {
		return new Point(pixX*PIXEL_SIZE, pixY*PIXEL_SIZE);
	}
	private Complex compPosToFracPos(int compX, int compY) {
		return new Complex(fracTopLeft.re() + compX*fracOverComp, fracTopLeft.im() + compY*fracOverComp, ImMath.coordinateSystem.CARTISAN);
	}
	private Point FracPosToPixPos(Complex fracPos) {
		return new Point((int) ((fracPos.re()-fracTopLeft.re())*compOverFrac/PIXEL_SIZE), (int) ((fracPos.im()-fracTopLeft.im())*compOverFrac/PIXEL_SIZE));
	}
	
	private void initPixelValues() {
		pixels = new Pixel[pixWidth][pixHeight];
		for (int pixX = 0; pixX < pixWidth; pixX++) {
			for (int pixY = 0; pixY < pixHeight; pixY++) {
				//int compX = pixX*PIXEL_SIZE;
				//int compY = pixY*PIXEL_SIZE;
				
				Point compPos = pixPosToCompPos(pixX, pixY);
				Complex fracPos = compPosToFracPos((int) compPos.getX(), (int) compPos.getY());
				
				pixels[pixX][pixY] = new Pixel(fracPos);
				
//				pixelPositions[pixX][pixY] = positionValue;
//				pixelValues[pixX][pixY] = fractal.start(positionValue);
//				pixelConverges[pixX][pixY] = true;
//				pixelColors[pixX][pixY] = CONVERGE_COLOR;
			}
		}
	}
	private void incrementPixelValues() {
		for (int x = 0; x < pixels.length; x++) {
			for (int y = 0; y < pixels[x].length; y++) {
				if (!pixels[x][y].hasResult) {
					pixels[x][y].advanceFracTarget();
				}
			}
		}
	}
	
	private Color colorOf(int iteration) {
		//cycles through each of the colors switching every 10 iterations
		return COLOR_PATTERN[(iteration/ITERATIONS_PER_COLOR)%COLOR_PATTERN.length];
	}
	
	protected void incrementIterations() {
		iterations++;
		//currentColor = colorOf(iterations);
		
		incrementPixelValues();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (started) {
			for (int pixX = 0; pixX < pixels.length; pixX++) {
				for (int pixY = 0; pixY < pixels[pixX].length; pixY++) {
					Point compPos = pixPosToCompPos(pixX, pixY);
					
					g.setColor(pixels[pixX][pixY].getColor());
					
					g.fillRect((int) compPos.getX(), (int) compPos.getY(), PIXEL_SIZE, PIXEL_SIZE);//draws a point
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
			if (started) {
				Complex newCenter = compPosToFracPos(mouseX, mouseY);
				double newFracDiameter;
				if (arg0.isControlDown()) {
					newFracDiameter = fracDiameter*ZOOM_FACTOR;
				}else {
					newFracDiameter = fracDiameter/ZOOM_FACTOR;
				}
				
				initForPosition(newFracDiameter, newCenter);
			}
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
			if (started) {
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
	
	private class Pixel {
		private Complex fracPosition;
		
		private Complex fracTarget;
		private Pixel target;
		private boolean isEnd;
		
		private int lastIteration;
		private boolean hasResult;
		private boolean diverged;
		
		public Pixel(Complex fracPosition) {
			this.fracPosition = fracPosition;
			fracTarget = fractal.start(fracPosition);
			
			target = this; isEnd = true;
			
			lastIteration = iterations;
			hasResult = false;
			diverged = true;
		}
		
		private void advancePixTarget() {
			Pixel currentPix = this;
			
			while (!currentPix.isEnd) {
				currentPix = currentPix.target;
			}
			
			lastIteration = currentPix.lastIteration;
			hasResult = currentPix.hasResult;
			diverged = currentPix.diverged;
		}
		public void advanceFracTarget() {
			if (isEnd) {
				
				fracTarget = fractal.step(fracTarget, fracPosition);
				//TODO: make all this code work even when there are no pixels to go to
				Point pixPos = FracPosToPixPos(fracTarget);
				if (pixPos.getX() < 0 || pixPos.getY() < 0 || pixPos.getX() >= pixWidth || pixPos.getY() >= pixHeight) {
					if (fractal.diverges(fracTarget, lastIteration)) {
						hasResult = true;
						diverged = true;
					}
				}else {
					Pixel hitPixel = pixels[(int) pixPos.getX()][(int) pixPos.getY()];
					hitPixel.advancePixTarget();
					
					if (hitPixel.target == this) {
						hasResult = true;
						diverged = false;
					}else {
						isEnd = false; target = hitPixel.target;
					}
				}
			}else {
				advancePixTarget();
				target.advanceFracTarget();
			}
			
			lastIteration = iterations;
		}
//		public Pixel getTarget() {
//			advancePixTarget();
//			return target;
//		}
//		public boolean getIsEnd() {
//			return isEnd;
//		}
		public Color getColor() {
			if (hasResult) {
				if (diverged) {
					return colorOf(lastIteration);
				}else {
					return CONVERGE_COLOR;
				}
			}else {
				return UNCERTAIN_COLOR;
			}
		}
	}
}