/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto.util;

/**
 * A Collection of BreakerCommands (The Entire Path)
 * Ran through the BreakerCommandScheduler
 */
public abstract class BreakerPath {
	
	/** The Actions for the path */
	public BreakerPathAction[] pathActions = new BreakerPathAction[30];
	
	/** The number of Actions in the path */
	public int pathActionsLength = 0;
	
	/** Add an action to the Path */
	public void add(BreakerPathAction action) { add(action, false); }
	/** Add an action to the Path.
	 * @param runWithLastAction If this command should run with the action before it (if available)  */
	public void add(BreakerPathAction action, boolean runWithLastAction) {
		pathActions[pathActionsLength] = action;
		pathActions[pathActionsLength].runWithLastAction = runWithLastAction;
		pathActionsLength++;
	}
}
