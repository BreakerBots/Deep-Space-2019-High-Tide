/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystems.drive;

import frc.team5104.util.managers.Subsystem;

/** Nothing here :1 */
class DriveLooper extends Subsystem.Looper {
	//Loop
	protected void update() { }

	//Stop Everything
	protected void disabled() { Drive.stop(); }
	protected void enabled() { }
}
