package clisk;

import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Class representing a Vector function transform constructed from clisk functions.
 * 
 * @author Mike
 *
 */
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
	
	public Function getFunction(int i) {
		return functions[i];
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<outputDimensions; i++) {
			double v=functions[i].calc(source);
			dest.set(i,v);
		}
	}
	
	public void transform(AVector source, Vector3 dest) {
		dest.x=functions[0].calc(source);
		dest.y=functions[1].calc(source);
		dest.z=functions[2].calc(source);
	}
	
	public void transform(Vector3 source, Vector3 dest) {
		dest.x=functions[0].calc(source);
		dest.y=functions[1].calc(source);
		dest.z=functions[2].calc(source);
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
