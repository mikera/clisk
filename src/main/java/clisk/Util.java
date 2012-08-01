package clisk;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import clojure.lang.Compiler;

public class Util {
	
	

	public static BufferedImage newImage(int w, int h) {
		BufferedImage result=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		return result;
	}
	
	public static BufferedImage scaleImage(BufferedImage img, int w, int h) {
		int sw = img.getWidth();
		int sh = img.getHeight();
		BufferedImage result=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D) result.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);		
		Rectangle r = new Rectangle(0, 0, sw, sh);
	    //g.setPaint(new TexturePaint(img, r));
		//g.fill(new Rectangle (0,0,w ,h));
		g.drawImage(img, 0, 0, w, h, null);
		return result;
	}
	
	public static int clamp (int v) {
		if (v<0) return 0;
		if (v>255) return 255;
		return v;
	}
	
	public static int toARGB(double r, double g, double b) {
		return getARGBQuick(
				clamp((int)(r*256)),
				clamp((int)(g*256)),
				clamp((int)(b*256)),
				255);
	}
	
	public static int toARGB(double r, double g, double b, double a) {
		return getARGBQuick(
				clamp((int)(r*256)),
				clamp((int)(g*256)),
				clamp((int)(b*256)),
				clamp((int)(a*256)));
	}
	
	public static int getARGBQuick(int r, int g, int b, int a) {
		return (a<<24)|(r<<16)|(g<<8)|b;
	} 
	
	@SuppressWarnings("serial")
	public static JFrame frame(final Image image) {
		JFrame f=new JFrame("Clisk Image");
		
		JMenuBar menuBar=new JMenuBar();
		JMenu menu=new JMenu("File");
		menuBar.add(menu);
		JMenuItem jmi=new JMenuItem("Save As...");	
		menu.add(jmi);
		
		JComponent c=new JComponent() {
			public void paint(Graphics g) {
				g.drawImage(image,0,0,null);
			}
		};
		c.setMinimumSize(new Dimension(image.getWidth(null),image.getHeight(null)));
		f.setMinimumSize(new Dimension(image.getWidth(null)+100,image.getHeight(null)+50));
		f.add(c);
		f.setJMenuBar(menuBar);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return f;
	}	
	
	/**
	 * Shows an image in a new clisk JFrame
	 * @param image
	 * @return
	 */
	public static JFrame show(final Image image) {
		JFrame f=frame(image);
		f.setVisible(true);
		f.pack();		
		return f;
	}	
	
	/**
	 * Shows an image generated from the given script in a new clisk JFrame
	 * @param script
	 * @return
	 */
	public static JFrame show(String script) {
		return show(Generator.generate(script));
	}	
	
	public static final long longHash(long a) {
		a ^= (a << 21);
		a ^= (a >>> 35);
		a ^= (a << 4);
		return a;
	}
	
	public static final long hash (double x) {
		return longHash(longHash(
				0x8000+Long.rotateLeft( longHash(Double.doubleToRawLongBits(x)),17)));
	}
	
	public static final long hash (double x, double y) {
		return longHash(longHash(
				hash(x)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(y)),17)));
	}
	
	public static final long hash (double x, double y, double z) {
		return longHash(longHash(
				hash(x,y)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(z)),17)));
	}
	
	public static final long hash (double x, double y, double z, double t) {
		return longHash(longHash(
				hash(x,y,z)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(t)),17)));
	}
	
	private static final double LONG_SCALE_FACTOR=1.0/(Long.MAX_VALUE+1.0);
	
	public static final double dhash(double x) {
		long h = hash(x);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y) {
		long h = hash(x,y);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y, double z) {
		long h = hash(x,y,z);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y , double z, double t) {
		long h = hash(x,y,z,t);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}

	public static Object execute(String script) {
		return Compiler.load(new StringReader(script));
	}
	
	private static double componentFromPQT(double p, double q, double t) {
		 t=Maths.mod(t, 1.0);
         if (t < 1.0/6.0) return p + (q - p) * 6.0 * t;
         if (t < 0.5) return q;
         if (t < 2.0/3.0) return p + (q - p) * (2.0/3.0 - t) * 6;
         return p;
	}
	
	public static double redFromHSL(double h, double s, double l) {
		if (s==0.0) return l;
	    double q = (l < 0.5) ? l * (1 + s) : l + s - l * s;
        double p = (2 * l) - q;
        return componentFromPQT(p, q, h + 1.0/3.0);
	}
	
	public static double greenFromHSL(double h, double s, double l) {
		if (s==0.0) return l;
	    double q = (l < 0.5) ? l * (1 + s) : l + s - l * s;
        double p = (2 * l) - q;
        return componentFromPQT(p, q, h);
	}
	
	public static double blueFromHSL(double h, double s, double l) {
		if (s==0.0) return l;
	    double q = (l < 0.5) ? l * (1 + s) : l + s - l * s;
        double p = (2 * l) - q;
        return componentFromPQT(p, q, h - 1.0/3.0);
	}
}
