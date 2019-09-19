package frc.team5104.subsystems.intake;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class Intake extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Intake"; }
	private IntakeInterface _interface = new IntakeInterface();
	protected Interface getInterface() { return _interface; }
	private IntakeLooper _looper = new IntakeLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//open()
		//close()
		//isOpen()
		//isClosed()
		//setWheelSpeed(enum incake wheel speed)
		//getWheelSpeed(ret enum incake wheel speed)
}
