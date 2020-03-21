
/**
 * Represents a complex number. This class is immutable. Supports both
 * angle/magnitude and real/imaginary representations and automatically converts
 * between the two. Minimizes unnecessary calculations. Restricts angle to
 * values between negative pi and positive pi , but
 * supports any other angle used as an input. Restricts magnitude to positive
 * magnitudes but supports any other magnitude input. Zero magnitude defaults
 * angle to 0.
 * 
 * @author Samuel Lieberman
 */
public class Complex {
	private static final boolean DEBUG = false;
	
	/**
	 * Sets the coordinate system.
	 */
	private static ImMath.coordinateSystem coordMode = ImMath.coordinateSystem.CARTISAN;
	public static void setCoordMode(ImMath.coordinateSystem coordMode) {
		Complex.coordMode = coordMode;
	}
	/**
	 * Gets the coordinate system.
	 * @return the current coordinate system.
	 */
	public static ImMath.coordinateSystem getCoordMode() {
		return coordMode;
	}
	
	private double real;
	private double imaginary;
	private boolean hasReal;
	private boolean hasImaginary;
	
	private double magnitude;
	private double angle;
	private boolean hasMagnitude;
	private boolean hasAngle;
	
	/**
	 * creates a complex number using n1 and n2 as the real and imaginary values
	 * respectively (in the case of coordMode==CARTISAN) or using n1 and n2 as
	 * magnitude and angle respectively (in the case of coordMode==POLAR). new
	 * Complex(x1, x2) works identically to new Complex(x1, x2,
	 * Complex.getCoordMode());
	 * 
	 * @param x1 the real value OR the magnitude
	 * @param x2 the imaginary value OR the angle
	 */
	public Complex(double x1, double x2) {
		this(x1, x2, coordMode);
	}
	
	/**
	 * private static helper class. Always returns a value between lo and hi. If the
	 * range is the difference between lo and hi, then the end result will always be
	 * an integer multiple of the range off from the original value x
	 * 
	 * @param x  the number to mod between lo and hi.
	 * @param lo the minimum value of the result
	 * @param hi tha maximum value of the result
	 * @return a value r between lo and hi such that x = (hi-lo)*k + r for some
	 *         integer k
	 */
	private static double modBetween(double x, double lo, double hi) {
		double range = hi-lo;
		
		return ((x - lo)%range + range)%range + lo; 
	}
	/**
	 * creates a complex number using n1 and n2 as the real and imaginary values
	 * respectively (in the case of coordMode==CARTISAN) or using n1 and n2 as
	 * magnitude and angle respectively (in the case of coordMode==POLAR). new
	 * Complex(x1, x2) works identically to new Complex(x1, x2,
	 * Complex.getCoordMode());
	 * 
	 * @param x1 the real value OR the magnitude
	 * @param x2 the imaginary value OR the angle
	 */
	public Complex(double x1, double x2, ImMath.coordinateSystem coordMode) {
		if (Double.isNaN(x1)) {
			throw new IllegalStateException("x1 is NaN");
		}
		if (Double.isNaN(x2)) {
			throw new IllegalStateException("x2 is NaN");
		}
		
		hasReal = false;
		hasImaginary = false;
		hasMagnitude = false;
		hasAngle = false;
		
		switch (coordMode) {
		case CARTISAN:
			real = x1;
			imaginary = x2;
			
			hasReal = true;
			hasImaginary = true;
			break;
		case POLAR:
			magnitude = Math.abs(x1);
			if (magnitude < 0) {
				angle = modBetween(x2 + Math.PI, -Math.PI, Math.PI);
			}else if (magnitude == 0) {
				angle = 0;
			}else {
				angle = modBetween(x2, -Math.PI, Math.PI);
			}
			
			hasMagnitude = true;
			hasAngle = true;
			break;
		default:
			throw new UnsupportedOperationException("coordinate system \"" + coordMode + "\" not supported");
		}
		
		if (DEBUG) {
			checkIllegalState();
		}
	}
	
	/**
	 * Use this before setting anything
	 * 
	 * Checks for illegal states including:
	 * not enough information to uniquely identify an imaginary number
	 */
	private void checkIllegalState() {
		if ((!hasReal || !hasImaginary) && (!hasMagnitude || !hasAngle)) {
			throw new IllegalStateException("too many unknowns");
		}
		if (hasReal && Double.isNaN(real)) {
			throw new IllegalStateException("real is " + real);
		}
		if (hasImaginary && Double.isNaN(imaginary)) {
			throw new IllegalStateException("imaginary is " + imaginary);
		}
		if (hasMagnitude && Double.isNaN(magnitude)) {
			throw new IllegalStateException("magnitude is " + magnitude);
		}
		if (hasAngle && Double.isNaN(angle)) {
			throw new IllegalStateException("angle is " + angle);
		}
		if (hasAngle && angle < -Math.PI || angle > Math.PI) {
			throw new IllegalStateException("invalid angle: \"" + angle + "\"");
		}
		if (hasMagnitude && magnitude < 0) {
			throw new IllegalStateException("invalid magnitude: \"" + magnitude + "\"");
		}
	}
	
	private void setReal() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		double cos = Math.cos(th());
		if (cos == 0) {
			real = 0;
		}else {
			real = r()*cos;
		}
		
		hasReal = true;
	}
	private void setImaginary() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		double sin = Math.sin(th());
		if (sin == 0) {
			imaginary = 0;
		}else {
			imaginary = r()*sin;
		}
		
		hasImaginary = true;
	}
	
	private void setMagnitude() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		magnitude = Math.sqrt(Math.pow(re(), 2) + Math.pow(im(), 2));
		
		hasMagnitude = true;
	}
	private void setAngle() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (Double.isInfinite(im()) && Double.isInfinite(re())) {
				throw new RuntimeException("angle is undefined for this complex number since it's real and imaginary values are infinite");
		}else {
			angle = Math.atan2(im(), re());
		}
		
		hasAngle = true;
	}
	
	/**
	 * @return the real value of this complex number
	 */
	public double re() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (!hasReal) {
			setReal();
		}

		if (DEBUG) {
			checkIllegalState();
		}
		
		return real;
	}
	/**
	 * @return the imaginary value of this complex number
	 */
	public double im() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (!hasImaginary) {
			setImaginary();
		}
		
		if (DEBUG) {
			checkIllegalState();
		}
		
		return imaginary;
	}
	
	/**
	 * @return the magnitude of this complex number
	 */
	public double r() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (!hasMagnitude) {
			setMagnitude();
		}

		if (DEBUG) {
			checkIllegalState();
		}
		
		return magnitude;
	}
	/**
	 * @return the angle of this complex number
	 */
	public double th() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (!hasAngle) {
			setAngle();
		}

		if (DEBUG) {
			checkIllegalState();
		}
		
		return angle;
	}
	
	/**
	 * returns true if the real value is infinite, the imaginary value is infinite, or the magnitude is infinite
	 * @return
	 */
	public boolean isInfinite() {
		return Double.isInfinite(re()) || Double.isInfinite(im()) || Double.isInfinite(r());
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (DEBUG) {
			checkIllegalState();
		}
		
		if (arg0 instanceof Complex) {
			Complex c = (Complex) arg0;
			
			if (hasReal && hasImaginary) {
				return real == c.re() && imaginary == c.im();
			}else {
				return magnitude == c.r() && angle == c.th();
			}
		}else {
			return false;
		}
	}
	
	public String toString() {
		if (DEBUG) {
			checkIllegalState();
		}
		
		switch (coordMode) {
		case CARTISAN:
			return re() + " + " + im() + "*i";
		case POLAR:
			return r() + "*e^(i*" + th() + ")";
		default:
			throw new UnsupportedOperationException("coordinate system \"" + coordMode + "\" not supported");
		}
	}
}
