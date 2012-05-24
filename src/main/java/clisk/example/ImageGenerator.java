package clisk.example;

import java.awt.image.BufferedImage;
import clisk.Generator;
import clisk.Util;

public class ImageGenerator {

	public static void main(String[] args) {
		BufferedImage b1=Generator.generate("vplasma",256,256);		
		Util.show(b1);
		
		BufferedImage b2=Generator.generate(
				"(viewport [-1 -1] [1 1] (v* (vwarp globe (v* [1 0.6 0] plasma)) (light-value [-1 -1 1] (height-normal globe))))"
				,256,256);		
		Util.show(b2);
		
		// necessary to stop Clojure agents thread before program exits
		Util.execute("(shutdown-agents)");

	}
}
