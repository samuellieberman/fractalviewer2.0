
public class JuliaSet_phi extends JuliaSet{
	private static final Complex ADD = new Complex(1 - (1+Math.sqrt(5))/2, 0);
	@Override
	public Complex ADD() {
		return ADD;
	}
	
	@Override
	public String getName() {
		return "Julia Set 1-phi";
	}
}
