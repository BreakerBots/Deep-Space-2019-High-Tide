package frc.team5104.teleop;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;

/** Manages all the calls for all Teleop Controllers given */
public class TeleopControllerManager {
	private static TeleopController[] targetTeleopControllers;
	
	/** Tell the State Machine Manager what State Machines to use */
	public static void useTeleopControllers(TeleopController... teleopControllers) {
		targetTeleopControllers = teleopControllers;
	}
	
	/** Call periodically when the robot is enabled (and wants teleoperation) */
	public static void update() {
		for (TeleopController teleopController : targetTeleopControllers) {
			try {
				//Call teleop controller function
				teleopController.update();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
}
