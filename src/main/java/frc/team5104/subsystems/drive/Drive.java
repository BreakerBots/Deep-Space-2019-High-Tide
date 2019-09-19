package frc.team5104.subsystems.drive;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class Drive extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Drive"; }
	private DriveInterface _interface = new DriveInterface();
	protected Interface getInterface() { return _interface; }
	private DriveLooper _looper = new DriveLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//set(drive signal)
		//stop
		//get encoder
		//reset encoder
		//get gyro
		//reset gyro
}
