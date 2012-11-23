package edu.buffalo.cse.cse605.project2;

public interface PartialFuturableReturnType {

	@PartialFuturableGetter
	public double getValue(int x, int y);
	
	@PartialFuturableSetter
	public void setValue(int x, int y, double value);
}
