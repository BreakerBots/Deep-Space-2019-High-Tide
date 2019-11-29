package frc.team5104.vision;

import frc.team5104.Superstructure;
import frc.team5104.Superstructure.GamePiece;
import frc.team5104.Superstructure.Height;
import frc.team5104.Superstructure.Mode;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.DriveSignal.DriveUnit;
import frc.team5104.util.WebappTuner.tunerInput;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;
import frc.team5104.vision.Limelight.LEDMode;
import frc.team5104.vision.Limelight.CamMode;

public class VisionManager {

	//INITIAL: Turn to the target, drive forward slowly
	@tunerInput
	private static double VISION_INITIAL_TURN_P = 0.5;
	@tunerInput
	private static double VISION_INITIAL_FORWARD_KV = 3;
	@tunerInput
	private static double VISION_INITIAL_TARGET_X = 2.2;
	@tunerInput
	private static double VISION_INITIAL_TOL_X = 0.5;
	
	//FORWARD: Drive forward to the wall, turn to keep centered on target
	@tunerInput
	private static double VISION_FORWARD_TURN_P = 0.2;
	@tunerInput
	private static double VISION_FORWARD_FORWARD_P = 1;
	@tunerInput
	private static double VISION_FORWARD_FORWARD_PE = 0.5;
	@tunerInput
	private static double VISION_FORWARD_FORWARD_SUB = 0.4;
	@tunerInput
	private static double VISION_FORWARD_TARGET_X = 2.2;
	@tunerInput
	private static double VISION_FORWARD_TARGET_Y_NORMAL = 0;
	@tunerInput
	private static double VISION_FORWARD_TARGET_Y_EARLY = 4.0;
	@tunerInput
	private static double VISION_FORWARD_TARGET_Y_VERY_EARLY = 12.0;
	@tunerInput
	private static double VISION_FORWARD_TOL_Y = 1;

	//FINAL: Adjust the robot until flush
	@tunerInput
	private static double VISION_FINAL_TURN_P = 0.25;
	@tunerInput
	private static double VISION_FINAL_TARGET_X = 2.2;
	@tunerInput
	private static double VISION_FINAL_TOL_X = 2;
	
	//Other Constants/Variables
	private static enum VisionState { INITIAL, FORWARD, FINAL, FINISHED }
	private static VisionState visionState = VisionState.FINISHED;
	private static VisionState lastVisionState = VisionState.FINISHED;
	private static final int VISION_LOST_TARGET_EXIT_COUNT = 20;
	private static long stateStartTime = System.currentTimeMillis();
	private static int lostTargetCount;
	private static double lastX, lastY;
	
	//Update Loop
	public static DriveSignal getNextDriveSignal() {
		//Read x, y values and set to last values if can't read value
		double x = Limelight.getTargetX();
		x = x == 5104 ? lastX : x;
		double y = Limelight.getTargetY();
		y = y == 5104 ? lastY : y;
		
		//No Target Automatically Exit
		if (!Limelight.hasTarget()) {
			lostTargetCount++;
			if (lostTargetCount > VISION_LOST_TARGET_EXIT_COUNT) {
				console.log(c.VISION, t.ERROR, "Lost Vision Target");
				visionState = VisionState.FINISHED;
				return new DriveSignal();
			}
		}
		
		//Track Keep of State Timing
		if (lastVisionState != visionState)
			stateStartTime = System.currentTimeMillis();
		
		//Handle States
		DriveSignal returnSignal = new DriveSignal();
		switch (visionState) {
			//Final Turn
			case INITIAL: {
				if (Math.abs(VISION_INITIAL_TARGET_X - x) < VISION_INITIAL_TOL_X && System.currentTimeMillis() > stateStartTime + 200)
					visionState = VisionState.FORWARD;
				
				double errorX = VISION_INITIAL_TARGET_X - x;
				double turn = VISION_INITIAL_TURN_P * errorX;
				double forward = VISION_INITIAL_FORWARD_KV;
				returnSignal = new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
				break;
			}
				
			//Forward
			case FORWARD: {
				double targetY = VISION_FORWARD_TARGET_Y_NORMAL;
				if (Superstructure.getMode() != Mode.INTAKE && 
					(Superstructure.getHeight() == Height.L2 || Superstructure.getHeight() == Height.L3) &&
					Superstructure.getGamePiece() == GamePiece.HATCH) {
					targetY = VISION_FORWARD_TARGET_Y_EARLY;
				}
				if (Superstructure.getMode() != Mode.INTAKE && 
					Superstructure.getHeight() != Height.SHIP &&
					Superstructure.getGamePiece() == GamePiece.CARGO) {
					targetY = VISION_FORWARD_TARGET_Y_VERY_EARLY;
				}
				
				if (Math.abs(targetY - y) < VISION_FORWARD_TOL_Y)
					visionState = VisionState.FINAL;
				
				double errorX = VISION_FORWARD_TARGET_X - x;
				double turn = VISION_FORWARD_TURN_P * errorX;
				double errorY = targetY - y;
				double forward = VISION_FORWARD_FORWARD_P * Math.pow(-errorY-VISION_FORWARD_FORWARD_SUB, VISION_FORWARD_FORWARD_PE);
				returnSignal = new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
				break;
			}
				
			//Final Turn
			case FINAL: {
				if (Math.abs(VISION_FINAL_TARGET_X - x) < VISION_FINAL_TOL_X)
					visionState = VisionState.FINISHED;
				
				double errorX = VISION_FINAL_TARGET_X - x;
				double turn = VISION_FINAL_TURN_P * errorX;
				returnSignal = new DriveSignal(-turn, turn, true, DriveUnit.voltage);
				break;
			}
			default: break;
		}
		
		//Save last Values
		lastX = Limelight.getTargetX();
		lastY = Limelight.getTargetY();
		lastVisionState = visionState;
		
		//Return Drive Value
		returnSignal.leftSpeed = BreakerMath.clamp(returnSignal.leftSpeed, -4, 4);
		returnSignal.rightSpeed = BreakerMath.clamp(returnSignal.rightSpeed, -4, 4);
		return returnSignal;
	}

	//Getters
	public static boolean isFinished() { return visionState == VisionState.FINISHED; }
	public static boolean isInVision() { return !isFinished(); }

	//Start and End
	public static void end() {
		Limelight.setLEDMode(LEDMode.OFF);
		Limelight.setcamMode(CamMode.DRIVE);
	}
	public static void start() {
		Limelight.setLEDMode(LEDMode.ON);
		Limelight.setcamMode(CamMode.VISION);
		visionState = VisionState.INITIAL;
		stateStartTime = System.currentTimeMillis();
		lostTargetCount = 0;
		lastX = 0;
		lastY = 0;
	}
}