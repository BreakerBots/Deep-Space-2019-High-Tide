/*BreakerBots Robotics Team 2019*/
package frc.team5104.statemachines;

/** Father State Machine for the Intake, Wrist, and Elevator */
public class IWE extends StateMachine {
	protected String getName() { return "IWE"; }

	//States
	public static enum IWEState { IDLE, INTAKE, PLACE, EJECT }
	public static enum IWEGamePiece { CARGO, HATCH }
	public static enum IWEHeight { L1, L2, L3 }
	private static IWEState targetState = IWEState.IDLE;
	private static IWEGamePiece targetGamePiece = IWEGamePiece.HATCH;
	private static IWEHeight targetHeight = IWEHeight.L1;
	
	//Functions
	public static void setTargetState(IWEState desiredState) { targetState = desiredState; }
	public static void setTargetGamePiece(IWEGamePiece desiredGamePiece) { targetGamePiece = desiredGamePiece; }
	public static void setTargetHeight(IWEHeight desiredHeight) { targetHeight = desiredHeight; }
	public static IWEState getTargetState() { return targetState; }
	public static IWEGamePiece getTargetGamePiece() { return targetGamePiece; }
	public static IWEHeight getTargetHeight() { return targetHeight; }

	//Tell Subsystems what to do
	protected void update() {
		
	}
	
	//Other
	protected void enabled() { }
	protected void disabled() { }
}
