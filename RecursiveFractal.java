public interface RecursiveFractal {
	public Complex start(Complex initial);
	public Complex step(Complex c, Complex initial);
	public boolean diverges(Complex c, int iterations);
	
	public String getName();
	public String getFormula();
	
	public Complex getInitialScreenCenter();
	public double getInitialScreenDiameter();
}
