package clisk;

import clojure.lang.IFn;
import clojure.lang.Compiler;

import java.awt.image.BufferedImage;
import java.io.StringReader;


public class Generator {
	private static final String NAMESPACE="clisk.demo";

	private static IFn imageGenerator=(IFn) Compiler.load(new StringReader("(use '"+NAMESPACE+") clisk.node/img"));

	public static BufferedImage generate(String script) {
		return generate(script,256,256);
	}
	
	public static BufferedImage generate(String script, int width, int height) {
		script = "(in-ns '"+NAMESPACE+") "+script;
		Object result = Util.execute(script);
		if (result instanceof BufferedImage) {
			return (BufferedImage)result;
		} else {
			return (BufferedImage)imageGenerator.invoke(result,width,height);
		}
	}
	
	/**
	 * Testing main function
	 * @param args
	 */
	public static void main(String[] args) {
		Util.show(generate("vplasma"));
		Util.execute("(shutdown-agents)");
	}
}
