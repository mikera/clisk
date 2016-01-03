package clisk;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.APrimitiveVector;
import mikera.vectorz.Vector1;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vector4;
import clojure.lang.ISeq;

public class Function implements IFunction, clojure.lang.IFn {
	@Override
	public double calc() {
		return calc(0.0);
	}
	
	@Override
	public double calc(double x) {
		return calc(x,0.0);
	}
	
	@Override
	public double calc(double x, double y) {
		return calc(x,y,0.0);
	}

	@Override
	public double calc(double x, double y, double z) {
		return calc(x,y,z,0.0);
	}
	
	public double calc(AVector v) {
		if (v  instanceof APrimitiveVector) {
			switch (v.length()) {
				case 1: return calc((Vector1)v);
				case 2: return calc((Vector2)v);
				case 3: return calc((Vector3)v);
				case 4: return calc((Vector4)v);
			}
		}
		switch (v.length()) {
			case 1: return calc(v.get(0));
			case 2: return calc(v.get(0),v.get(1));
			case 3: return calc(v.get(0),v.get(2),v.get(3));
			case 4: return calc(v.get(0),v.get(2),v.get(3),v.get(4));
			default: throw new CliskError("Cannot calculate function on source vector with dimensionality "+v.length());
		}
	}
	
	public double calc(Vector1 v) {
		return calc(v.x);
	}

	public double calc(Vector2 v) {
		return calc(v.x,v.y);
	}
	
	public double calc(Vector3 v) {
		return calc(v.x,v.y,v.z);
	}
	
	public double calc(Vector4 v) {
		return calc(v.x,v.y,v.z,v.t);
	}
	
	@Override
	public double calc(double x, double y, double z, double t) {
		throw new Error("Function not implemented!");
	}
	
	private static double db(Object num) {
		return ((Number)num).doubleValue();
	}
	
	@Override
	public Object invoke() {
		return calc();
	}
	
	@Override
	public Object invoke(Object x) {
		return calc(db(x));
	}
	
	@Override
	public Object invoke(Object x, Object y) {
		return calc(db(x),db(y));
	}
	
	@Override
	public Object invoke(Object x, Object y, Object z) {
		return calc(db(x),db(y),db(z));
	}
	
	@Override
	public Object invoke(Object x, Object y, Object z, Object t) {
		return calc(db(x),db(y),db(z),db(t));
	}

	@Override
	public Object call() throws Exception {
		return invoke();
	}

	@Override
	public void run() {
		invoke();
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13, Object arg14) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16, Object arg17) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16, Object arg17, Object arg18) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16, Object arg17,
			Object arg18, Object arg19) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16, Object arg17,
			Object arg18, Object arg19, Object arg20) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4,
			Object arg5, Object arg6, Object arg7, Object arg8, Object arg9,
			Object arg10, Object arg11, Object arg12, Object arg13,
			Object arg14, Object arg15, Object arg16, Object arg17,
			Object arg18, Object arg19, Object arg20, Object... args) {
		throw new Error("Unsupported arity!");
	}

	@Override
	public Object applyTo(ISeq arglist) {
		throw new Error("Unsupported arity!");
	}
}
