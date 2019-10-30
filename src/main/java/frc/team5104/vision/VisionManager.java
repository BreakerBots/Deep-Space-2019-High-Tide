package frc.team5104.vision;

import frc.team5104.subsystems.drive.DriveObjects.DriveSignal;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnit;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.WebappTuner.tunerInput;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;
import frc.team5104.vision.Limelight.LEDMode;

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
	private static double VISION_FORWARD_FORWARD_P = 0.8;
	@tunerInput
	private static double VISION_FORWARD_FORWARD_PE = 0.5;
	@tunerInput
	private static double VISION_FORWARD_FORWARD_SUB = 0.4;
	@tunerInput
	private static double VISION_FORWARD_TARGET_X = 2.2;
	@tunerInput
	private static double VISION_FORWARD_TARGET_Y = 10.0;
	@tunerInput
	private static double VISION_FORWARD_TOL_Y = 0.5;

	//FINAL: Adjust the robot until flush
	@tunerInput
	private static double VISION_FINAL_TURN_P = 0.01;
	@tunerInput
	private static double VISION_FINAL_FORWARD_KV = 0.3;
	@tunerInput
	private static double VISION_FINAL_TARGET_X = 2.2;
	@tunerInput
	private static double VISION_FINAL_TOL_X = 0.5;
	
	//Other Constants/Variables
	private static enum VisionState { INITIAL, FORWARD, FINAL, FINISHED }
	private static VisionState visionState = VisionState.INITIAL;
	private static VisionState lastVisionState = VisionState.INITIAL;
	private static final int VISION_LOST_TARGET_EXIT_COUNT = 20;
	private static long stateStartTime = System.currentTimeMillis();
	private static int lostTargetCount;
	private static double lastX, lastY;
	
	//Initialize
	public static void init() {
		Limelight.init();
		Limelight.setLEDMode(LEDMode.ON);
		visionState = VisionState.INITIAL;
		stateStartTime = System.currentTimeMillis();
		lostTargetCount = 0;
		lastX = 0;
		lastY = 0;
	}

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
		DriveSignal returnSignal;
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
				if (Math.abs(VISION_FORWARD_TARGET_Y - y) < VISION_FORWARD_TOL_Y)
					visionState = VisionState.FINAL;
				
				double errorX = VISION_FORWARD_TARGET_X - x;
				double turn = VISION_FORWARD_TURN_P * errorX;
				double errorY = VISION_FORWARD_TARGET_Y - y;
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
				double forward = VISION_FINAL_FORWARD_KV;
				returnSignal = new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
				break;
			}
			
			default: {
				returnSignal = new DriveSignal();
				break;
			}
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

	public static boolean isFinished() {
		return visionState == VisionState.FINISHED;
	}

	public static void end() {
		Limelight.setLEDMode(LEDMode.OFF);
	}
}
