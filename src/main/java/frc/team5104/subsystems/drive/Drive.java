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
	protected static DriveInterface _interface = new DriveInterface();
	protected Interface getInterface() { return _interface; }
	private DriveLooper _looper = new DriveLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
	public static void set(DriveSignal signal) {
		//shifting
		_interface.shift(signal.isHighGear);
		
		//motor speed
		switch (signal.unit) {
			case percentOutput: {
				_interface.set(
						signal.leftSpeed, 
						signal.rightSpeed, 
						ControlMode.PercentOutput
					);
				break;
			}
			case feetPerSecond: {
				_interface.set(
						DriveUnits.feetPerSecondToTalonVel(signal.leftSpeed), 
						DriveUnits.feetPerSecondToTalonVel(signal.rightSpeed), 
						ControlMode.Velocity
					);
				break;
			}
			case voltage: {
				_interface.set(
						signal.leftSpeed / _interface.getLeftGearboxVoltage(),
						signal.rightSpeed / _interface.getRightGearboxVoltage(),
						ControlMode.PercentOutput
					);
				break;
			}
			case stop:
				_interface.stop();
				break;
		}
	}
	public static void stop() { _interface.stop(); }
	public static DriveEncoders getEncoders() { return _interface.getEncoders(); }
	public static void resetEncoders() { _interface.resetEncoders(); }
	public static double getGyro() { return _interface.getGyro(); }
	public static void resetGyro() { _interface.resetGyro(); }
}
