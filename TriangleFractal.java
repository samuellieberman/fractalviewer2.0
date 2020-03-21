
/**
 * My own fractal.  The inverse of the Mandelbrot Set.
 * 
 * @author Samuel Lieberman
 *
 */
public class TriangleFractal implements RecursiveFractal {
	private static final Complex START_POS = new Complex(0, 0);
	private static final double DIVERGE_RADIUS = 0.01;
	
	@Override
	public Complex start(Complex initial) {
		return ImMath.ZERO;
	}

	@Override
	public Complex step(Complex c, Complex initial) {
		//return ImMath.add(ImMath.mult(c, c), initial);
		return ImMath.add(ImMath.pow(c, -2), initial);
	}

	@Override
	public boolean diverges(Complex c, int iterations) {
		return c.r() < DIVERGE_RADIUS;// && Math.abs(c.im()) < DIVERGE_RADIUS;
	}

	@Override
	public String getName() {
		return "Triangle Fractal";
	}

	@Override
	public String getFormula() {
		return "Z_n+1 = (Z_n)^-2 + C";
	}

	@Override
	public Complex getInitialScreenCenter() {
		return START_POS;
	}

	@Override
	public double getInitialScreenDiameter() {
		return 1;//DIVERGE_RADIUS*2;
	}
}
