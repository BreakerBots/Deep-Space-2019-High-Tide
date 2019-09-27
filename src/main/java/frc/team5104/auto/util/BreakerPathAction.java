/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto.util;

/**
 * A Single BreakerCommand (ind. peices of a path)
 */
public abstract class BreakerPathAction {
	/** Called when action is started to be run */
	public abstract void init();
	
	/** Called periodically when the action is being run
	 * @return If the action is finished */
	public abstract boolean update();
	
	/** Called when the action is finished being run */
	public abstract void end();
	
	/** If this command should run with the action before it (if available) */
	public boolean runWithLastAction = false;
}
