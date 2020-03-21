
/**
 * The driver class for this program. Starts up the GUI. Takes no command line
 * arguments.
 * 
 * @author Samuel Lieberman
 *
 */
public class FractalDriver {
	public static void main(String[] args) {
		if (args.length != 0) {
			System.out.println("No command line arguments are currently supported.");
			printUsage();
			
			System.exit(1);
		}
		
		new FractalGUI();
	}
	
	private static void printUsage() {
		System.out.println("Usage: java FractalDriver");
	}
}
