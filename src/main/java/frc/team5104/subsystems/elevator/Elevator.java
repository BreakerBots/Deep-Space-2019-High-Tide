package frc.team5104.subsystems.elevator;

import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class Elevator extends Subsystem.Actions {
	//Meta
	protected String getName() { return "Elevator"; }
	static ElevatorInterface _interface = new ElevatorInterface();
	protected Interface getInterface() { return _interface; }
	private static ElevatorLooper _looper = new ElevatorLooper();
	protected Looper getLooper() { return _looper; }
	
	//Actions (mostly in IWE)
	public static boolean encoderDisconnected() { return _interface.encoderDisconnected(); }
	public static int getMillisAtL3() { return (int) (System.currentTimeMillis() - _looper.elevatorPositionStartTime); }
	public static boolean lowerLimitSwitchHit() { return _interface.lowerLimitSwitchHit(); }
}
