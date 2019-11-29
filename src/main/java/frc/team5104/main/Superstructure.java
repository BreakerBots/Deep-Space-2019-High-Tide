/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.subsystems.Elevator;
import frc.team5104.subsystems.Intake;
import frc.team5104.subsystems.Wrist;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/** 
 * The Superstructure is a massive state machine that handles the Intake, Wrist, and Elevator
 * The Superstructure only controls the states... its up the subsystems to figure out what to do
 * based on the state of the Superstructure.
 */
public class Superstructure {
	//States and Variables
	public static enum Mode { IDLE, INTAKE, PLACE_READY, PLACE, EJECT }
	public static enum GamePiece { CARGO, HATCH }
	public static enum Height { L1, L2, L3, SHIP }
	public static enum SystemState { CALIBRATING, MANUAL, AUTONOMOUS, DISABLED }
	private static Mode targetMode;
	private static GamePiece targetGamePiece;
	private static Height targetHeight;
	private static SystemState systemState;
	public static boolean cargoIntakeGround = true;
	private static long modeStart = System.currentTimeMillis();
	private static long systemStateStart = System.currentTimeMillis();
	
	//External Functions
	public static Mode getMode() { return targetMode; }
	public static void setMode(Mode targetMode) { 
		Superstructure.targetMode = targetMode;
		modeStart = System.currentTimeMillis();
	}
	public static SystemState getSystemState() { return systemState; }
	public static void setSystemState(SystemState systemState) { 
		Superstructure.systemState = systemState;
		systemStateStart = System.currentTimeMillis();
	}
	public static GamePiece getGamePiece() { return targetGamePiece; }
	public static void setGamePiece(GamePiece targetGamePiece) { Superstructure.targetGamePiece = targetGamePiece; }
	public static Height getHeight() { return targetHeight; }
	public static void setHeight(Height targetHeight) { Superstructure.targetHeight = targetHeight; }

	//Manage States
	static void update() {
		//Automatically Enter Manual
		if (getSystemState() == SystemState.AUTONOMOUS && (Wrist.encoderDisconnected() || Elevator.encoderDisconnected())) {
			console.error(c.SUPERSTRUCTURE, "encoder error... disabling");
			setSystemState(SystemState.DISABLED);
		}
		
		//Exit Eject (if in eject and has been ejecting for long enough)
		if (getMode() == Mode.EJECT && (System.currentTimeMillis() > modeStart + Constants.INTAKE_EJECT_TIME)) {
			console.log(c.SUPERSTRUCTURE, "finished eject... idling");
			setMode(Mode.IDLE);
		}
		
		//Exit Intake (if in intake and has desired game piece)
		if (getMode() == Mode.INTAKE && (getGamePiece() == GamePiece.HATCH ? Intake.hasHatch() : Intake.hasCargo())) {
			console.log(c.SUPERSTRUCTURE, "finished intake... idling");
			setMode(Mode.IDLE);
			Controls.INTAKE_RUMBLE.start();
		}
		
		//Exit Calibration
		if (Elevator.lowerLimitHit() && Wrist.backLimitSwitchHit()) {
			console.log(c.SUPERSTRUCTURE, "finished calibration... entering autonomous");
			setSystemState(SystemState.AUTONOMOUS);
		}
		if (System.currentTimeMillis() > systemStateStart + 7000) {
			console.error(c.SUPERSTRUCTURE, "error in calibration... disabling");
			setSystemState(SystemState.DISABLED);
		}
		
		//Set Elevator Height
		if (getSystemState() == SystemState.AUTONOMOUS) {
			if (getGamePiece() == GamePiece.CARGO && getMode() == Mode.INTAKE && !cargoIntakeGround)
				Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_CARGO_WALL;
			else if (getMode() == Mode.INTAKE || getMode() == Mode.IDLE)
				Elevator.targetElevatorHeight = 0;
			else if (getGamePiece() == GamePiece.CARGO) {
				if (getHeight() == Height.L1)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_CARGO_L1;
				else if (getHeight() == Height.L2)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_CARGO_L2;
				else if (getHeight() == Height.L3)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_CARGO_L3;
				else if (getHeight() == Height.SHIP)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_CARGO_SHIP;
			}
			else {
				if (getHeight() == Height.L2)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_HATCH_L2;
				else if (getHeight() == Height.L3)
					Elevator.targetElevatorHeight = Constants.ELEVATOR_HEIGHT_HATCH_L3;
				else Elevator.targetElevatorHeight = 0;
			}
		}
		else Elevator.targetElevatorHeight = 0;
		
		//Set Wrist Height
		if (getSystemState() == SystemState.AUTONOMOUS) {
			if (getMode() == Mode.IDLE || !Elevator.isRoughlyAtTargetHeight()) 
				Wrist.targetWristAngle = 0;
			else if (getGamePiece() == GamePiece.HATCH) {
				if (getMode() == Mode.INTAKE)
					Wrist.targetWristAngle = Constants.WRIST_ANGLE_HATCH_INTAKE;
				else Wrist.targetWristAngle = Constants.WRIST_ANGLE_HATCH_EJECT;
			}
			else {
				if (getMode() == Mode.INTAKE) {
					if (cargoIntakeGround)
						Wrist.targetWristAngle = Constants.WRIST_ANGLE_CARGO_INTAKE_GROUND;
					else Wrist.targetWristAngle = Constants.WRIST_ANGLE_CARGO_INTAKE_WALL;
				}
				else {
					if (getHeight() == Height.SHIP)
						Wrist.targetWristAngle = Constants.WRIST_ANGLE_CARGO_EJECT_SHIP;
					else Wrist.targetWristAngle = Constants.WRIST_ANGLE_CARGO_EJECT_ROCKET;
				}
			}
		}
		else Wrist.targetWristAngle = 0;
	}
	static void enabled() { setToDefaultStates(); }
	static void disabled() { setToDefaultStates(); }
	private static void setToDefaultStates() {
		targetMode = Mode.IDLE;
		systemState = SystemState.CALIBRATING;
	}
}