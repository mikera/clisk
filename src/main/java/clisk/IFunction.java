package clisk;

/**
 * Interface for a multi-dimensional function that returns a single double value.
 */
public interface IFunction {
	public double calc();
	
	public double calc(double x);
	
	public double calc(double x, double y);

	public double calc(double x, double y, double z);
	
	public double calc(double x, double y, double z, double t);
}
