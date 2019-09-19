package frc.team5104.subsystems.wrist;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class Wrist extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Intake"; }
	private WristInterface _interface = new WristInterface();
	protected Interface getInterface() { return _interface; }
	private WristLooper _looper = new WristLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//set position(enum wristpos)
		//get position(ret enum wristpos)
		//setManualMode(boolean)
		//getManualMode(ret boolean)
		//set Speed (if manual mode, double speed)
}
