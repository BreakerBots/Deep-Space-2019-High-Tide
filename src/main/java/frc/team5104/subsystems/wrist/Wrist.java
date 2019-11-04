package frc.team5104.subsystems.wrist;

import frc.team5104.main.Constants;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.Subsystem.Interface;
import frc.team5104.util.managers.Subsystem.Looper;

public class Wrist extends Subsystem.Actions {
	//Metadata
	protected String getName() { return "Wrist"; }
	protected static WristInterface _interface = new WristInterface();
	protected Interface getInterface() { return _interface; }
	protected static WristLooper _looper = new WristLooper();
	protected Looper getLooper() { return _looper; }
	
	//Actions (mostly in IWE)
	public static boolean encoderDisconnected() { return _interface.encoderDisconnected(); }
	public static boolean backLimitSwitchHit() { return _interface.backLimitSwitchHit(); }
	public static double getEncoder() { return _interface.getEncoderAngle(); }
	public static boolean isAtTargetAngle() { return Math.abs(_interface.getEncoderAngle() - _interface.lastTargetAngle) < Constants.WRIST_ANGLE_TOL; }
}