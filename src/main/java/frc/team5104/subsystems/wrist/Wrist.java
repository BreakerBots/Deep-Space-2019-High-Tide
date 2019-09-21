package frc.team5104.subsystems.wrist;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.Subsystem.Interface;
import frc.team5104.subsystems.Subsystem.Looper;

public class Wrist extends Subsystem.Actions {
	//Metadata
	protected String getName() { return "Wrist"; }
	protected static WristInterface _interface = new WristInterface();
	protected Interface getInterface() { return _interface; }
	private static WristLooper _looper = new WristLooper();
	protected Looper getLooper() { return _looper; }

	//Actions
		//Autonomous
	public static boolean setPosition(WristPosition position) {
		if (_looper.wristState == WristState.AUTONOMOUS) {
			_looper.wristPosition = position;
			return true;
		}
		return false;
	}
	public static WristPosition getPosition() { return _looper.wristPosition; }
	
		//States
	public static boolean setState(WristState desiredState) { 
		if (_looper.wristState != WristState.CALIBRATING && desiredState != WristState.CALIBRATING) {
			_looper.wristState = desiredState;
			return true;
		}
		return false;
	}
	public static WristState getState() { return _looper.wristState; }
	
		//Manual
	public static boolean setPercentOutput(double percentOutput) {
		if (_looper.wristState == WristState.MANUAL) {
			_interface.setPercentOutput(percentOutput);
			return true;
		}
		return false;
	}
	
	//Enums
	static enum WristState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum WristPosition { BACK, UP, CARGO_INTAKE_WALL, CARGO_INTAKE_GROUND, HATCH_PLACE_ANGLED, CARGO_PLACE_ANGLED }
}
