/**
 * Performs operations on complex numbers.
 * 
 * @author Samuel Lieberman
 *
 */
public class ImMath {
	public static enum coordinateSystem {CARTISAN, POLAR};
	
	public static final Complex ZERO = new Complex(0, 0);//0
	public static final Complex POS_1 = new Complex(1, 0);//1
	public static final Complex POS_2 = new Complex(2, 0);//2
	public static final Complex NEG_1 = new Complex(-1, 0);//-1
	public static final Complex POS_I = new Complex(0, 1);//i
	public static final Complex NEG_I = new Complex(0, -1);//-i
	
	//cannot be instantiated
	private ImMath() {}
	
	public static Complex add(Complex... cs) {
		double realSum = 0;
		double imaginarySum = 0;
		
		for (Complex c:cs) {
			try {
				c.im();
			}catch (IllegalStateException e) {
				System.out.println(e.getMessage());
			}
			
			realSum += c.re();
			imaginarySum += c.im();
		}
		
		return new Complex(realSum, imaginarySum, coordinateSystem.CARTISAN);
	}
	
	public static Complex sub(Complex c1, Complex c2) {
		return new Complex(c1.re() - c2.re(), c1.im() - c2.im());
	}
	
	public static Complex mult(Complex... cs) {
		return mult(1, cs);
	}
	public static Complex mult(double n, Complex... cs) {
		double realProduct = n;
		double imaginaryProduct = 0;
		
		for (Complex c:cs) {
			double nextReal = realProduct*c.re() - imaginaryProduct*c.im();
			double nextImaginary = imaginaryProduct*c.re() + realProduct*c.im();
			
			realProduct = nextReal;
			imaginaryProduct = nextImaginary;
		}
		
		return new Complex(realProduct, imaginaryProduct, coordinateSystem.CARTISAN);
	}
	
	public static Complex pow(Complex c, double x) {
		if (c.r() < 0.000000001) {
			return ZERO;
		}
		
		if (Double.isInfinite(c.r())) {
			return ZERO;//TODO change
		}
		
		Complex oldExp = new Complex(Math.log(c.r()), c.th(), coordinateSystem.CARTISAN);
		Complex newExp = mult(x, oldExp);
		Complex answer = new Complex(Math.pow(Math.E, newExp.re()), newExp.im(), coordinateSystem.POLAR);
		return answer;
	}
	public static Complex pow(Complex c1, Complex c2) {
		if (c1.r() < 0.000000001) {
			return ZERO;
		}
		
		if (Double.isInfinite(c1.r())) {
			return ZERO;//TODO change
		}
		
		Complex oldExp = new Complex(Math.log(c1.r()), c1.th(), coordinateSystem.CARTISAN);
		Complex newExp = mult(oldExp, c2);
		Complex answer = new Complex(Math.pow(Math.E, newExp.re()), newExp.im(), coordinateSystem.POLAR);
		return answer;
	}
	
	public static Complex Div(Complex c1, Complex c2) {
		return mult(c1, pow(c2, NEG_1));
	}
}
