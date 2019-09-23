package frc.team5104.subsystems.intake;

import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class Intake extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Intake"; }
	static IntakeInterface _interface = new IntakeInterface();
	protected Interface getInterface() { return _interface; }
	private static IntakeLooper _looper = new IntakeLooper();
	protected Looper getLooper() { return _looper; }
	
	//Actions (Mostly in IWE)
	public static boolean hasHatch() {
		return _interface.hasHatch();
	}
	
	public static boolean hasCargo() {
		return _interface.hasCargo();
	}
}
