package frc.team5104.subsystems.elevator;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class Elevator extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Elevator"; }
	private ElevatorInterface _interface = new ElevatorInterface();
	protected Interface getInterface() { return _interface; }
	private ElevatorLooper _looper = new ElevatorLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//set position (enum IWEHeight)
		//get position (ret enum IWEHeight)
		//setManualMode(boolean)
		//getManualMode(ret boolean)
		//set Speed (if manual mode, double speed)
}
