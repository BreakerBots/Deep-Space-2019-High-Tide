package frc.team5104.vision;

import frc.team5104.subsystems.drive.DriveObjects.DriveSignal;
import frc.team5104.subsystems.drive.DriveObjects.DriveUnit;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;
import frc.team5104.vision.Limelight.LEDMode;

public class VisionManager {

	//INITIAL: Turn to the target, drive forward slowly
	private static final double VISION_INITIAL_TURN_P = 0.5;
	private static final double VISION_INITIAL_FORWARD_KV = 2;
	private static final double VISION_INITIAL_TARGET_X = 0;
	private static final double VISION_INITIAL_TOL_X = 0;
	
	//FORWARD: Drive forward to the wall, turn to keep centered on target
	private static final double VISION_FORWARD_TURN_P = 0.1;
	private static final double VISION_FORWARD_FORWARD_P = 1;
	private static final double VISION_FORWARD_TARGET_X = 0;
	private static final double VISION_FORWARD_TARGET_Y = 0;
	private static final double VISION_FORWARD_TOL_Y = 0;

	//FINAL: Adjust the robot until flush
	private static final double VISION_FINAL_TURN_P = 0.5;
	private static final double VISION_FINAL_FORWARD_KV = 4;
	private static final double VISION_FINAL_TARGET_X = 0;
	private static final double VISION_FINAL_TOL_X = 0;
	
	//Other Constants/Variables
	private static enum VisionState { INITIAL, FORWARD, FINAL, FINISHED }
	private static VisionState visionState = VisionState.INITIAL;
	private static final int VISION_LOST_TARGET_EXIT_COUNT = 20;
	private static int lostTargetCount;
	private static double lastX, lastY;
	
	//Initialize
	public static void init() {
		Limelight.init();
		Limelight.setLEDMode(LEDMode.ON);
		visionState = VisionState.INITIAL;
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
		
		//Handle States
		switch (visionState) {
			//Final Turn
			case INITIAL: {
				if (Math.abs(x) < VISION_INITIAL_TOL_X)
					visionState = VisionState.FORWARD;
				
				double errorX = VISION_INITIAL_TARGET_X - x;
				double turn = VISION_INITIAL_TURN_P * errorX;
				double forward = VISION_INITIAL_FORWARD_KV;
				return new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
			}
				
			//Forward
			case FORWARD: {
				if (Math.abs(y) < VISION_FORWARD_TOL_Y)
					visionState = VisionState.FINAL;
				
				double errorX = VISION_FORWARD_TARGET_X - x;
				double turn = VISION_FORWARD_TURN_P * errorX;
				double errorY = VISION_FORWARD_TARGET_Y - y;
				double forward = VISION_FORWARD_FORWARD_P * errorY;
				return new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
			}
				
			//Final Turn
			case FINAL: {
				if (Math.abs(x) < VISION_FINAL_TOL_X)
					visionState = VisionState.FINISHED;
				
				double errorX = VISION_FINAL_TARGET_X - x;
				double turn = VISION_FINAL_TURN_P * errorX;
				double forward = VISION_FINAL_FORWARD_KV;
				return new DriveSignal(forward - turn, forward + turn, true, DriveUnit.voltage);
			}
			
			default: break;
		}
		
		//Save last Values
		lastX = Limelight.getTargetX();
		lastY = Limelight.getTargetY();
		
		//Default Stop
		return new DriveSignal();
	}

	public static boolean isFinished() {
		return visionState == VisionState.FINISHED;
	}

	public static void end() {
		Limelight.setLEDMode(LEDMode.OFF);
	}

}
