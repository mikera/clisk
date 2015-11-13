package clisk;

/**
 * Interface for a multi-dimensional function that returns an RGB value.
 */
public interface IRenderFunction {
	public int calc();
	
	public int calc(double x);
	
	public int calc(double x, double y);

	public int calc(double x, double y, double z);
	
	public int calc(double x, double y, double z, double t);
}
