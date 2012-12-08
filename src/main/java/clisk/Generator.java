package clisk;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import java.awt.image.BufferedImage;


public class Generator {
	private static final Var REQUIRE=RT.var("clojure.core", "require");
	static {
		REQUIRE.invoke(Symbol.intern("clisk.node"));
		REQUIRE.invoke(Symbol.intern("clisk.samples.demo"));
	}
	private static final Var IMG=RT.var("clisk.node", "img");
	
	/**
	 * Generates a Clisk image from a script
	 */
	public static BufferedImage generate(String script) {
		return generate(script,256,256);
	}
	
	public static BufferedImage generate(String script, int width, int height) {
		script = "(in-ns 'clisk.smaples.demo) "+script;
		Object result = Util.execute(script);
		if (result instanceof BufferedImage) {
			return (BufferedImage)result;
		}
		
		return (BufferedImage)IMG.invoke(result,width,height);
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
