/*BreakerBots Robotics Team 2019*/
package frc.team5104.subsystems.drive;

import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.SubsystemManager.DebugMessage;

/** Nothing here :1 */
class DriveLooper extends Subsystem.Looper {
	//Loop
	protected void update() { }

	//Debug
	protected DebugMessage debug() {
		return new DebugMessage(
				"encoders: ", "{" + Drive._interface.getEncoders().toString() + "}",
				"gyro: ", Drive._interface.getGyro(),
				"voltage-draw-left", Drive._interface.getLeftGearboxVoltage(),
				"voltage-draw-rite", Drive._interface.getRightGearboxVoltage()
			);
	}
	
	//Stop Everything
	protected void disabled() { Drive.stop(); }
	protected void enabled() { }
}
