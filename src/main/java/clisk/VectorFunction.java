package clisk;

import mikera.vectorz.AVector;

public class VectorFunction extends mikera.vectorz.functions.VectorFunction {
	private int inputDimensions;
	private int outputDimensions;
	private Function[] functions;
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<outputDimensions; i++) {
			double v=functions[i].calc(source);
			dest.set(i,v);
		}
	}

	@Override
	public int inputDimensions() {
		return inputDimensions;
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

}
