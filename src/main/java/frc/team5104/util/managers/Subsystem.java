/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

/** 
 * A snickers rapper of all the requirements of a subsystem. 
 */
public abstract class Subsystem {
	/** Return the name of this subsystem (for prints) */
	protected abstract String getName();
	/** Called when robots boots up; initialize devices here */
	protected abstract void init();
	/** Called whenever the robot becomes enabled */
	protected abstract void enabled();
	/** Called periodically from the robot loop */
	protected abstract void update();
	/** Called whenever the robot becomes disabled; stop all devices here */
	protected abstract void disabled();
}
