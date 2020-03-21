/**
 * Represents a Julia set. Extend by implementing the ADD() method, which should
 * return the complex offset. Represented by Z_(n+1) = (Z_n)^2 + ADD
 * 
 * @author Samuel Lieberman
 *
 */
public abstract class JuliaSet implements RecursiveFractal {
	private static final Complex START_POS = new Complex(0, 0);
	private static final double DIVERGE_RADIUS = 2;
	public abstract Complex ADD();
	public boolean IS_COMPLEX() {
		return false;
	}
	public Complex POWER_COM() {
		return new Complex(2, 0);
	}
	public int POWER_RE() {
		return 2;
	}
	
	@Override
	public Complex start(Complex initial) {
		return initial;
	}

	@Override
	public Complex step(Complex c, Complex initial) {
		
		return ImMath.add(ImMath.pow(c, 2), ADD());
	}

	@Override
	public boolean diverges(Complex c, int iterations) {
		return c.r() > DIVERGE_RADIUS;// && Math.abs(c.im()) < DIVERGE_RADIUS;
	}

	@Override
	public String getName() {
		if (IS_COMPLEX()) {
			return "Julia Set^(" + POWER_COM() + ")  " + ADD();
		}else if (POWER_RE() != 2) {
			return "Julia Set^" + POWER_RE() + "  " + ADD();
		}else {
			return "Julia Set " + ADD();//1-phi";
		}
	}

	@Override
	public String getFormula() {
		if (IS_COMPLEX()) {
			return "Z_n+1 = (Z_n)^(" + POWER_COM() + ") + " + ADD();
		}else {
			return "Z_n+1 = (Z_n)^" + POWER_RE() + " + " + ADD();
		}
		
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
