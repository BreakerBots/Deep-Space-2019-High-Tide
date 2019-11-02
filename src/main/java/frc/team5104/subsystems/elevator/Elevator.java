package frc.team5104.subsystems.elevator;

import frc.team5104.main.Constants;
import frc.team5104.subsystems.canifier.CANifier;
import frc.team5104.util.WebappTuner.tunerOutput;
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
	public static boolean lowerLimitSwitchHit() { return CANifier.elevatorLowerLimitHit(); }
	@tunerOutput
	public static double getMotorOutputPercent() { return _interface.getMotorPercentOutput() * 100; }
	@tunerOutput
	public static double getEncoderHeight() { return _interface.getEncoderHeight(); }
	@tunerOutput
	public static double getTargetEncoderHeight() { return _interface.lastTargetHeight; }
	public static boolean isAtTargetHeight() { return Math.abs(getEncoderHeight() - getTargetEncoderHeight()) < Constants.ELEVATOR_HEIGHT_TOL; }
}
