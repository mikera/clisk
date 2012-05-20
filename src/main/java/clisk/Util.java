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

import javax.swing.JComponent;
import javax.swing.JFrame;

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
		JComponent c=new JComponent() {
			public void paint(Graphics g) {
				g.drawImage(image,0,0,null);
			}
		};
		c.setMinimumSize(new Dimension(image.getWidth(null),image.getHeight(null)));
		f.setMinimumSize(new Dimension(image.getWidth(null)+50,image.getHeight(null)+50));
		f.add(c);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return f;
	}	
	
	
	public static JFrame show(final Image image) {
		JFrame f=frame(image);
		f.setVisible(true);
		f.pack();		
		return f;
	}	
}
