package frc.team5104.subsystems.leds;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class LEDs extends Subsystem.Actions {
	//Meta
	protected String getName() { return "LEDs"; }
	private LEDsInterface _interface = new LEDsInterface();
	protected Interface getInterface() { return _interface; }
	private LEDsLooper _looper = new LEDsLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//display(enum led_display)
}
