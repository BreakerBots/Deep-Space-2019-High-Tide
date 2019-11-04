package frc.team5104.subsystems.canifier;

import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class CANifier extends Subsystem.Actions {
	//Meta
	protected String getName() { return "LEDs"; }
	static CANifierInterface _interface = new CANifierInterface();
	protected Interface getInterface() { return _interface; }
	private static CANifierLooper _looper = new CANifierLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
	public static void display(LEDsMode ledmode) {
		_looper.ledmode = ledmode;
	}
	public static boolean elevatorLowerLimitHit() {
		return !_interface.revLimitHit();
	}
	
	//Enum
	public enum LEDsMode {
		NONE,
		AUTO, VISION,
		INTAKE_SUCCESS, EJECT
	}
}
