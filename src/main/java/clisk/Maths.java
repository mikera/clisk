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
	
	public static double mod(double num, double div) {
		double result=num%div;
		if (result<0) result+=div;
		return result;
	}
	
	public static double square(double a) {
		return a*a;
	}
	
	public static double triangleWave(double a) {
		a-=Math.floor(a);
		return (a<0.5)?a*2:(2-a*2);
	}

}
