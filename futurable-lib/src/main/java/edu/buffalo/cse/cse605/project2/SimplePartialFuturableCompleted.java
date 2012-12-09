package edu.buffalo.cse.cse605.project2;

/*
 * A simple partial futurable completed implementation that only handles one invocation of futurable method host class at a time.
 */
public class SimplePartialFuturableCompleted implements PartialFuturableCompleted
{

	boolean completed = false;

	@Override
	public void setComplete(boolean complete)
	{
		completed = complete;
	}

	@Override
	public boolean getComplete()
	{
		return completed;
	}
}
