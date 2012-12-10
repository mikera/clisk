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
	private final IFunction[] functions;

	
	private VectorFunction(int inputs, int outputs, IFunction[] functions) {
		this.inputDimensions=inputs;
		this.outputDimensions=outputs;
		this.functions=functions;
	}
	
	public static VectorFunction create(int inputDims, List<IFunction> functions) {
		IFunction[] funcs=functions.toArray(new IFunction[functions.size()]);
		return new VectorFunction(inputDims,funcs.length,funcs);
	}
	
	public IFunction getFunction(int i) {
		return functions[i];
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<outputDimensions; i++) {
			double v;
			switch (source.length()) {
			case 0: v=functions[i].calc(); break;
			case 1: v=functions[i].calc(source.get(0)); break;
			case 2: v=functions[i].calc(source.get(0),source.get(1)); break;
			case 3: v=functions[i].calc(source.get(0),source.get(1),source.get(2)); break;
			default: v=functions[i].calc(source.get(0),source.get(1),source.get(2),source.get(3)); break;
			}
			
			dest.set(i,v);
		}
	}
	
	public void transform(AVector source, Vector3 dest) {
		dest.x=functions[0].calc(source.get(0),source.get(1),source.get(2));
		dest.y=functions[1].calc(source.get(0),source.get(1),source.get(2));
		dest.z=functions[2].calc(source.get(0),source.get(1),source.get(2));
	}
	
	public void transform(Vector3 source, Vector3 dest) {
		dest.x=functions[0].calc(source.x,source.y,source.z);
		dest.y=functions[1].calc(source.x,source.y,source.z);
		dest.z=functions[2].calc(source.x,source.y,source.z);
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
