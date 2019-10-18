package frc.team5104.subsystems.leds;

import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class LEDs extends Subsystem.Actions {
	//Meta
	protected String getName() { return "LEDs"; }
	static LEDsInterface _interface = new LEDsInterface();
	protected Interface getInterface() { return _interface; }
	private static LEDsLooper _looper = new LEDsLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
	public static void display(LEDsMode ledmode) {
		_looper.ledmode = ledmode;
	}
	
	//Enum
	public enum LEDsMode {
		NONE,
		AUTO, VISION,
		INTAKE_SUCCESS, EJECT
	}
}
