package edu.buffalo.cse.cse605.project2;

import org.jblas.DoubleMatrix;

@FuturableReturnType(hashingMethod=HashingMethod.TO_STRING)
public class PartialFuturableTestReturnTypeImpl implements PartialFuturableTestReturnType {

	private DoubleMatrix matrix = new DoubleMatrix(10,10);
	
	@Override
	@PartialFuturableGetter
	public double getValue(int x, int y) {
		return matrix.get(x,y);
	}

	@Override
	@PartialFuturableSetter
	public void setValue(int x, int y, @FuturableValue double value) {
		matrix.put(x, y, value);
	}

	@Override
	@PartialFuturableMarker
	public void markValue(int x, int y) {
		// Do nothing used mainly by futurable library to notify this "coordinate" is ready.
	}


}
