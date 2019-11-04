/*BreakerBots Robotics Team 2019*/
package frc.team5104.statemachines;

import frc.team5104.main.Constants;
import frc.team5104.main.Controls;
import frc.team5104.subsystems.elevator.Elevator;
import frc.team5104.subsystems.intake.Intake;
import frc.team5104.subsystems.wrist.Wrist;
import frc.team5104.util.Buffer;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.StateMachine;
import frc.team5104.vision.VisionManager;

/** Father State Machine for the Intake, Wrist, and Elevator */
public class IWE extends StateMachine {
	protected String getName() { return "IWE"; }
	
	//States and Variables
	public static enum IWEState { IDLE, INTAKE, PLACE, EJECT }
	public static enum IWEGamePiece { CARGO, HATCH }
	public static enum IWEHeight { L1, L2, L3, SHIP }
	public static enum IWEControl { MANUAL, AUTONOMOUS }
	public static enum IWESequence { NONE, VPEI }
	private static IWEState targetState;
	private static IWEGamePiece targetGamePiece;
	private static IWEHeight targetHeight;
	private static IWEControl control;
	private static IWESequence activeSequence;
	public static boolean cargoIntakeGround = true;
	private static long ejectStart = 0;
	public static double desiredWristManaul = 0;
	public static double desiredElevatorManaul = 0;
	private static Buffer intakeBuffer = new Buffer(10, false);
	
	//External Functions (Getters and Setters)
	public static IWEState getState() { return targetState; }
	public static void setState(IWEState targetState) { 
		IWE.targetState = targetState;
		if (targetState == IWEState.EJECT) ejectStart = System.currentTimeMillis();
	}
	public static IWEGamePiece getGamePiece() { return targetGamePiece; }
	public static void setGamePiece(IWEGamePiece targetGamePiece) { IWE.targetGamePiece = targetGamePiece; }
	public static IWEHeight getHeight() { return targetHeight; }
	public static void setHeight(IWEHeight targetHeight) { IWE.targetHeight = targetHeight; }
	public static IWEControl getControl() { return control; }
	public static void setControl(IWEControl control) { IWE.control = control; }
	public static IWESequence getActiveSequence() { return activeSequence; }
	public static void setActiveSequence(IWESequence activeSequence) { IWE.activeSequence = activeSequence; }
	public static void clearActiveSequence() { IWE.activeSequence = IWESequence.NONE; }
	
	//Manage States
	protected void update() {
		//Automatically Enter Manual
		if (getControl() == IWEControl.AUTONOMOUS && (Wrist.encoderDisconnected() || Elevator.encoderDisconnected())) {
			console.error(c.IWE, "ENCODER DISCONNECTED IN IWE SUBSYSTEM!!!");
			setControl(IWEControl.MANUAL);
		}
		
		//Place Eject Idle Sequence
		if (getActiveSequence() == IWESequence.VPEI) {
			if (VisionManager.isInVision()) {
				if (IWE.getHeight() == IWEHeight.L1 || IWE.getHeight() == IWEHeight.SHIP)
					IWE.setState(IWEState.PLACE);
				else IWE.setState(IWEState.IDLE);
			}
			else {
				if (getState() == IWEState.IDLE || getState() == IWEState.INTAKE) 
					setState(IWEState.PLACE);
				else if (getState() == IWEState.PLACE && Elevator.isAtTargetHeight() && Wrist.isAtTargetAngle()) 
					setState(IWEState.EJECT);
			}
		}
		
		//Exit Eject (if in eject and has been ejecting for long enough)
		if (getState() == IWEState.EJECT && (System.currentTimeMillis() > ejectStart + Constants.IWE_EJECT_TIME)) {
			console.log(c.IWE, "finished eject... idling");
			setState(IWEState.IDLE);
			clearActiveSequence();
		}
		
		//Exit Intake (if in intake and has desired game piece)
		if (getState() == IWEState.INTAKE && intakeBuffer.getBooleanOutput()) {
			console.log(c.IWE, "finished intake... idling");
			setState(IWEState.IDLE);
			Controls.IWE_INTAKE_RUMBLE.start();
		}
		intakeBuffer.update((getGamePiece() == IWEGamePiece.HATCH ? Intake.hasHatch() : Intake.hasCargo()));
	}
	protected void enabled() { setToDefaultStates(); }
	protected void disabled() { setToDefaultStates(); }
	protected void setToDefaultStates() {
		targetState = IWEState.IDLE;
		targetGamePiece = IWEGamePiece.HATCH;
		targetHeight = IWEHeight.L1;
		control = Constants.IWE_DEFAULT_CONTROL;
		activeSequence = IWESequence.NONE;
	}
}
