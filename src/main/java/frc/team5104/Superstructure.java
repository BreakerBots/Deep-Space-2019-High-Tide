/*BreakerBots Robotics Team 2019*/
package frc.team5104;

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
	public static enum IntakeMode { GROUND, WALL }
	private static Mode targetMode = Mode.IDLE;
	private static GamePiece targetGamePiece = GamePiece.HATCH;
	private static Height targetHeight = Height.L1;
	private static SystemState systemState = SystemState.DISABLED;
	private static IntakeMode intakeMode = IntakeMode.WALL;
	public static long modeStart = System.currentTimeMillis();
	public static long systemStateStart = System.currentTimeMillis();
	
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
	public static IntakeMode getIntakeMode() { return intakeMode; }
	public static void setIntakeMode(IntakeMode intakeMode) { Superstructure.intakeMode = intakeMode; }

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
		if (getSystemState() == SystemState.CALIBRATING) {
			if (Elevator.lowerLimitHit() && Wrist.backLimitSwitchHit()) {
				console.log(c.SUPERSTRUCTURE, "finished calibration... entering autonomous");
				setSystemState(SystemState.AUTONOMOUS);
			}
			else if (System.currentTimeMillis() > systemStateStart + 7000) {
				console.error(c.SUPERSTRUCTURE, "error in calibration... disabling");
				setSystemState(SystemState.DISABLED);
			}
		}
	}
	static void enabled() { setToDefaultStates(); }
	static void disabled() { setToDefaultStates(); }
	private static void setToDefaultStates() {
		targetMode = Mode.IDLE;
		systemState = SystemState.CALIBRATING;
	}
}