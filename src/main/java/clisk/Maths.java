package clisk;

import java.lang.Math;

public class Maths {
	
	/**
	 * Logistic sigmoid function in range [0..1]
	 * 
	 * @param a
	 * @return
	 */
	public static double sigmoid (double a) {
		double ea=Math.exp(-a);
		double df=(1/(1.0f+ea));
		if (Double.isNaN(df)) return (a>0)?1:0;
		return df;
	}

}
