package frc.team5104.subsystems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.team5104.subsystems.drive.DriveObjects.DriveEncoders;
import frc.team5104.subsystems.drive.DriveObjects.DriveSignal;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnits;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class Drive extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Drive"; }
	private DriveInterface _interface = new DriveInterface();
	protected Interface getInterface() { return _interface; }
	private DriveLooper _looper = new DriveLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
	public static void set(DriveSignal signal) {
		switch (signal.unit) {
			case percentOutput: {
				DriveInterface.set(
						signal.leftSpeed, 
						signal.rightSpeed, 
						ControlMode.PercentOutput
					);
				break;
			}
			case feetPerSecond: {
				DriveInterface.set(
						DriveUnits.feetPerSecondToTalonVel(signal.leftSpeed), 
						DriveUnits.feetPerSecondToTalonVel(signal.rightSpeed), 
						ControlMode.Velocity
					);
				break;
			}
			case voltage: {
				DriveInterface.set(
						signal.leftSpeed / DriveInterface.getLeftGearboxVoltage(),
						signal.rightSpeed / DriveInterface.getRightGearboxVoltage(),
						ControlMode.PercentOutput
					);
			}
		}
	}
	public static void stop() { DriveInterface.stop(); }
	public static DriveEncoders getEncoders() { return DriveInterface.getEncoders(); }
	public static void resetEncoders() { DriveInterface.resetEncoders(); }
	public static double getGyro() { return DriveInterface.getGyro(); }
	public static void resetGyro() { DriveInterface.resetGyro(); }
	public static boolean getShift() { return DriveInterface.getShift(); }
	public static void shift(boolean toHigh) { DriveInterface.shift(toHigh); }
}
