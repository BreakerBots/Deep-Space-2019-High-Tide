/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.subsystem;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

/** Manages the calls for all Subsystems given */
public class SubsystemManager {
	private static Subsystem[] targetSubsystems;
	
	/** Tell the Subsystem Manager what Subsystems to manage */
	public static void useSubsystems(Subsystem... availableSubsystems) {
		//Save all subsystems
		targetSubsystems = availableSubsystems;

		//Initialize Subsystem's Interface & Print out target subsystems
		String message = "Running Subsystems: ";
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.init();
				message += subsystem.getClass().getSimpleName() + ", ";
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
		console.log(c.MAIN, t.INFO, message.substring(0, message.length()-2));
	}
	
	/** Call to stop all subsystem */
	public static void stopAll() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.stop();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
	
	/** Call when the robot becomes enabled */
	public static void reset() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.reset();
				subsystem.stop();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
	
	/** Call periodically when the robot is enabled */
	public static void update() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.update();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
	
	/** Returns if all the subsystems are calibrated */
	public static boolean isCalibrated() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				if (!subsystem.isCalibrated())
					return false;
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
		return true;
	}
}
