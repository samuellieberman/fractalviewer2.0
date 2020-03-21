
/**
 * The classic Mandelbrot Set fractal.  Represented by Z_(n+1) = (Z_n)^2 + C where C is the starting position.
 * 
 * @author Samuel Lieberman
 *
 */
public class MandelbrotSet implements RecursiveFractal {
	private static final Complex START_POS = new Complex(0, 0);
	private static final double DIVERGE_RADIUS = 2;
	
	@Override
	public Complex start(Complex initial) {
		return ImMath.ZERO;
	}

	@Override
	public Complex step(Complex c, Complex initial) {
		return ImMath.add(ImMath.mult(c, c), initial);
		//return ImMath.add(ImMath.pow(c, POWER), initial);
	}

	@Override
	public boolean diverges(Complex c, int iterations) {
		return c.r() > DIVERGE_RADIUS;
	}

	@Override
	public String getName() {
		return "Mandelbrot Set";
	}

	@Override
	public String getFormula() {
		return "Z_n+1 = (Z_n)^2 + C";
	}

	@Override
	public Complex getInitialScreenCenter() {
		return START_POS;
	}

	@Override
	public double getInitialScreenDiameter() {
		return DIVERGE_RADIUS*2;
	}
}
