package clisk.generator;

import java.util.Random;

import clisk.IFunction;

public final class Voronoi2D {
	private final int count;
	
	private final double[] xs;
	private final double[] ys;
	
	public Voronoi2D(int numPoints) {
		count=numPoints;
		xs=new double[numPoints];
		ys=new double[numPoints];
		
		Random r=new Random();
		for (int i=0; i< count; i++) {
			xs[i]=r.nextDouble();
			ys[i]=r.nextDouble();
		}
	}
	
	private static double dist2(double x1, double y1, double x2, double y2) {
		double dx=x2-x1;
		dx-=Math.floor(dx);
		if (dx>0.5) dx=1-dx;
		double dy=y2-y1;
		dy-=Math.floor(dy);
		if (dy>0.5) dy=1-dy;
		return (dx*dx+dy*dy);
	}
	
	private int seek(double x, double y) {
		// indexes of best and next best
		int i0=0;
		int i1=1;
		
		// distances of best and next best
		double dd0=dist2(x,y,xs[0],ys[0]);
		double dd1=dist2(x,y,xs[1],ys[1]);
		
		if (dd1<dd0) {
			i0=1; i1=0;
			double t=dd0; dd0=dd1; dd1=t;
		}
		
		for (int i=2; i<count; i++) {
			double px=xs[i];
			double py=ys[i];
			double dd=dist2(px,py,x,y);
			if (dd<dd1) {
				if (dd<dd0) {
					i1=i0; 
					dd1=dd0;
					i0=i;  
					dd0=dd;
				} else {
					i1=i;
					dd1=dd;
				}
			}
		}
		return (i0 + (i1 << 16));
	}
	
	private int seekNearest(double x, double y) {
		return seek(x,y)&0xFFFF;
	}
	
	private int seekSecond(double x, double y) {
		return (seek(x,y)>>16)&0xFFFF;
	}
	
	public double nearestX(double x, double y) {
		return xs[seekNearest(x,y)];
	}
	
	public double nearestY(double x, double y) {
		return ys[seekNearest(x,y)];
	}
	
	public double nearestDistanceSquared(double x, double y) {
		int i=seekNearest(x,y);
		return dist2(x,y,xs[i],ys[i]);
	}
	
	public double nearestDistance(double x, double y) {
		return Math.sqrt(nearestDistanceSquared(x,y));
	}

	public double firstSecondFunction(double x, double y, IFunction func) {
		int i=seek(x,y);
		int i0=i&0xFFFF;
		int i1=(i>>16)&0xFFFF;
		double d0=Math.sqrt(dist2(x,y,xs[i0],ys[i0]));
		double d1=Math.sqrt(dist2(x,y,xs[i1],ys[i1]));
		return func.calc(d0, d1);
	}	
}
