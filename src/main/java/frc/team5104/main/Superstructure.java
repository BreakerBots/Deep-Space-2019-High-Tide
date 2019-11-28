/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.subsystems.elevator.ElevatorInterface;
import frc.team5104.subsystems.intake.Intake;
import frc.team5104.subsystems.intake.IntakeConstants;
import frc.team5104.subsystems.wrist.WristInterface;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/** Superstructure that handles a robot state for the Intake, Wrist, and Elevator */
public class Superstructure {
	//States and Variables
	public static enum SystemState { IDLE, INTAKE, PLACE_READY, PLACE, EJECT }
	public static enum GamePiece { CARGO, HATCH }
	public static enum Height { L1, L2, L3, SHIP }
	public static enum ControlMode { MANUAL, AUTONOMOUS }
	private static SystemState targetState;
	private static GamePiece targetGamePiece;
	private static Height targetHeight;
	private static ControlMode controlMode;
	public static boolean cargoIntakeGround = true;
	private static long ejectStart = 0;
	public static double desiredWristManaul = 0;
	public static double desiredElevatorManaul = 0;
	
	//External Functions
	public static SystemState getState() { return targetState; }
	public static void setState(SystemState targetState) { 
		Superstructure.targetState = targetState;
		if (targetState == SystemState.EJECT) ejectStart = System.currentTimeMillis();
	}
	public static GamePiece getGamePiece() { return targetGamePiece; }
	public static void setGamePiece(GamePiece targetGamePiece) { Superstructure.targetGamePiece = targetGamePiece; }
	public static Height getHeight() { return targetHeight; }
	public static void setHeight(Height targetHeight) { Superstructure.targetHeight = targetHeight; }
	public static ControlMode getControlMode() { return controlMode; }
	public static void setControlMode(ControlMode controlMode) { Superstructure.controlMode = controlMode; }

	//Manage States
	static void update() {
		//Automatically Enter Manual
		if (getControlMode() == ControlMode.AUTONOMOUS && (WristInterface.encoderDisconnected() || ElevatorInterface.encoderDisconnected())) {
			console.error(c.IWE, "!!! ENCODER DISCONNECTED !!!");
			setControlMode(ControlMode.MANUAL);
		}
		
		//Exit Eject (if in eject and has been ejecting for long enough)
		if (getState() == SystemState.EJECT && (System.currentTimeMillis() > ejectStart + IntakeConstants.INTAKE_EJECT_TIME)) {
			console.log(c.IWE, "finished eject... idling");
			setState(SystemState.IDLE);
		}
		
		//Exit Intake (if in intake and has desired game piece)
		if (getState() == SystemState.INTAKE && (getGamePiece() == GamePiece.HATCH ? Intake.hasHatch() : Intake.hasCargo())) {
			console.log(c.IWE, "finished intake... idling");
			setState(SystemState.IDLE);
			Controls.IWE_INTAKE_RUMBLE.start();
		}
	}
	static void enabled() { setToDefaultStates(); }
	static void disabled() { setToDefaultStates(); }
	private static void setToDefaultStates() {
		targetState = SystemState.IDLE;
		targetGamePiece = GamePiece.HATCH;
		targetHeight = Height.L1;
		controlMode = ControlMode.AUTONOMOUS;
	}
}
