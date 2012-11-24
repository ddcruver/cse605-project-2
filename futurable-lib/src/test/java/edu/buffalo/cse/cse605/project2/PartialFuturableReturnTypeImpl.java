package edu.buffalo.cse.cse605.project2;

import org.jblas.DoubleMatrix;

@FuturableReturnType(hashingMethod=HashingMethod.TO_STRING)
public class PartialFuturableReturnTypeImpl implements PartialFuturableReturnType {

	private DoubleMatrix matrix = new DoubleMatrix(10,10);
	
	@Override
	@PartialFuturableGetter
	public double getValue(int x, int y) {
		return matrix.get(x,y);
	}

	@Override
	@PartialFuturableSetter
	public void setValue(int x, int y, double value) {
		matrix.put(x, y, value);
	}
	


}
