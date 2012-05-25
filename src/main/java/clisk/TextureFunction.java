package clisk;

import java.awt.image.BufferedImage;

public class TextureFunction {
	private final BufferedImage texture;
	private final double xoff;
	private final double yoff;
	private final double w;
	private final double h;
	
	public TextureFunction (BufferedImage texture) {
		this (texture,0,0,texture.getWidth(),texture.getHeight());
	}

	public TextureFunction(BufferedImage texture, double x, double y, double width,
			double height) {
		this.texture=texture;
		this.xoff=x;
		this.yoff=y;
		this.w=width;
		this.h=height;
	}
	
	public int calc(double x, double y) {
		return texture.getRGB((int)(x*w+xoff),(int)(y*h+yoff));
	}
	
	public int calc(double x, double y, double z, double t) {
		return calc(x,y);
	}
	
}
