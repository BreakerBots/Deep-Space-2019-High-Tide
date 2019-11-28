/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.team5104.subsystems.drive.DriveConstants.DriveSignal;
import frc.team5104.subsystems.drive.DriveConstants.DriveUnits;
import frc.team5104.util.managers.Subsystem;

public class Drive extends Subsystem {
	protected String getName() { return "Drive"; }

	//Actions
	public static void set(DriveSignal signal) { currentDriveSignal = signal; }
	public static void stop() { currentDriveSignal = new DriveSignal(); }

	//Enabled, Disabled, Init
	protected void init() { DriveInterface.init(); }
	protected void enabled() {
		currentDriveSignal = new DriveSignal();
	}
	protected void disabled() {
		currentDriveSignal = new DriveSignal();
	}

	//Loop
	private static DriveSignal currentDriveSignal = new DriveSignal();
	protected void update() {
		DriveInterface.shift(currentDriveSignal.isHighGear);
		switch (currentDriveSignal.unit) {
			case percentOutput: {
				DriveInterface.set(
						currentDriveSignal.leftSpeed, 
						currentDriveSignal.rightSpeed, 
						ControlMode.PercentOutput,
						currentDriveSignal.feedForward
					);
				break;
			}
			case feetPerSecond: {
				DriveInterface.set(
						DriveUnits.feetPerSecondToTalonVel(currentDriveSignal.leftSpeed), 
						DriveUnits.feetPerSecondToTalonVel(currentDriveSignal.rightSpeed), 
						ControlMode.Velocity,
						currentDriveSignal.feedForward
					);
				break;
			}
			case voltage: {
				DriveInterface.set(
						currentDriveSignal.leftSpeed / DriveInterface.getLeftGearboxVoltage(),
						currentDriveSignal.rightSpeed / DriveInterface.getRightGearboxVoltage(),
						ControlMode.PercentOutput,
						currentDriveSignal.feedForward
					);
				break;
			}
			case stop:
				DriveInterface.stop();
				break;
		}
		currentDriveSignal = new DriveSignal();
	}
}