package edu.buffalo.cse.cse605.project2;

@FuturableReturnType(hashType=HashingMethod.TO_STRING)
public interface PartialFuturableReturnType {

	@PartialFuturableGetter
	public double getValue(int x, int y);
	
	@PartialFuturableSetter
	public void setValue(int x, int y, double value);
}
