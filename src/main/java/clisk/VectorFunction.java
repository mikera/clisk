package clisk;

import java.util.List;

import mikera.vectorz.AVector;

public class VectorFunction extends mikera.vectorz.functions.VectorFunction {
	private final int inputDimensions;
	private final int outputDimensions;
	private final Function[] functions;
	
	private VectorFunction(int inputs, int outputs, Function[] functions) {
		this.inputDimensions=inputs;
		this.outputDimensions=outputs;
		this.functions=functions;
	}
	
	public VectorFunction create(int inputDims, List<Function> functions) {
		Function[] funcs=functions.toArray(new Function[functions.size()]);
		return new VectorFunction(inputDims,funcs.length,funcs);
	}
	
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
