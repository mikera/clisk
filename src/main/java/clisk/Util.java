package clisk;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Util {

	public static BufferedImage newImage(int w, int h) {
		BufferedImage result=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
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
