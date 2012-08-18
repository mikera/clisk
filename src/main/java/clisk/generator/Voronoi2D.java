package clisk.generator;

import java.util.Random;

import clisk.IFunction;
import clisk.Util;

public final class Voronoi2D {
	private final int count;
	
	private final double[] xs;
	private final double[] ys;
	
	public Voronoi2D(int numPoints) {
		if (numPoints<3) throw new clisk.CliskError("Voronoi map must have at least 3 features");
		if (numPoints>1000) throw new clisk.CliskError("Voronoi map has maximum of 1000 features");

		count=numPoints;
		xs=new double[numPoints];
		ys=new double[numPoints];
		
		// use a fixed seed to ensure reproducibility
		Random r=new Random();
		r.setSeed(0xCAFEBABE ^ Util.longHash(numPoints));
		
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
		int i2=2;
		
		// distances of best and next best
		double dd0=dist2(x,y,xs[0],ys[0]);
		double dd1=dist2(x,y,xs[1],ys[1]);
		double dd2=dist2(x,y,xs[2],ys[2]);
		
		if (dd2<dd1) {
			i1=2; i2=1;
			double t=dd1; dd1=dd2; dd2=t;
		}
		
		if (dd1<dd0) {
			int ti=i0; i0=i1; i1=ti;
			double t=dd0; dd0=dd1; dd1=t;
		}
		
		for (int i=3; i<count; i++) {
			double px=xs[i];
			double py=ys[i];
			double dd=dist2(px,py,x,y);
			if (dd<dd2) {
				if (dd<dd1) {
					if (dd<dd0) {
						i2=i1;
						dd2=dd1;
						i1=i0; 
						dd1=dd0;
						i0=i;  
						dd0=dd;
					} else {
						i2=i1;
						dd2=dd1;
						i1=i;
						dd1=dd;
					}
				} else {
					i2=i;
					dd2=dd;
				}
			}
		}
		return (i0 + (i1 << 10) + (i2 << 20));
	}
	
	
	
	private int seekFirst(double x, double y) {
		return seek(x,y)&0x03FF;
	}
	
	private int seekSecond(double x, double y) {
		return (seek(x,y)>>10)&0x03FF;
	}
	
	private int seekThird(double x, double y) {
		return (seek(x,y)>>10)&0x03FF;
	}
	
	public double nearestX(double x, double y) {
		return xs[seekFirst(x,y)];
	}
	
	public double nearestY(double x, double y) {
		return ys[seekFirst(x,y)];
	}
	
	public double nearestDistanceSquared(double x, double y) {
		int i=seekFirst(x,y);
		return dist2(x,y,xs[i],ys[i]);
	}
	
	public double nearestDistance(double x, double y) {
		return Math.sqrt(nearestDistanceSquared(x,y));
	}

	public double firstSecondFunction(double x, double y, IFunction func) {
		int i=seek(x,y);
		int i0=i&0x03FF;
		int i1=(i>>10)&0x03FF;
		int i2=(i>>20)&0x03FF;
		double d0=Math.sqrt(dist2(x,y,xs[i0],ys[i0]));
		double d1=Math.sqrt(dist2(x,y,xs[i1],ys[i1]));
		double d2=Math.sqrt(dist2(x,y,xs[i2],ys[i2]));
		return func.calc(d0, d1, d2);
	}	
}
