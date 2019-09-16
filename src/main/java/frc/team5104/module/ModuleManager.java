/*BreakerBots Robotics Team 2019*/
package frc.team5104.module;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

/**
 * Manages the updating and handling of all BreakerSubsystems thrown into it
 */
public class ModuleManager {
	private static Module.Manager[] targets;
	
	/** Tell the Subsystem Manager what Subsystems to manage */
	public static void useModules(Module.Manager... modules) {
		targets = modules;
		
		String printOut = "Running Modules: ";
		
		//Initialize Module's Systems
		for (Module.Manager t : targets) {
			try {
				t.getSystems().init();
				printOut += t.getName() + " ";
			} catch (Exception e) {
				CrashLogger.logCrash(new Crash("main", e));
			}
		}
		
		console.log(c.MAIN, t.INFO, printOut);
	}
	
	/** Identify what modules and attached */
	public static Module.Manager[] identifyModules(Module.Manager... available_modules) {
		
		//Add All Attached Modules to Array
		Module.Manager[] attached_modules = new Module.Manager[available_modules.length];
		int attached_modules_index = 0;
		for (Module.Manager t : available_modules) {
			if (t.getSystems().isModuleAttached()) {
				attached_modules[attached_modules_index] = t;
				attached_modules_index++;
			}
		}
		
		//Shorten Array (remove null elements)
		Module.Manager[] attached_modules_fixed = new Module.Manager[attached_modules_index];
		for (int i = 0; i < attached_modules_index; i++) {
			attached_modules_fixed[i] = attached_modules[i];
		}
		
		return attached_modules_fixed;
	}
	
	/**
	 * CALL when the robot becomes enabled
	 */
	public static void enabled() {
		for (Module.Manager t : targets) {
			try {
				t.enabled();
			} catch (Exception e) {
				CrashLogger.logCrash(new Crash("main", e));
			}
		}
	}
	
	/**
	 * CALL periodically when the robot is enabled
	 */
	public static void handle() {
		try { update(); } catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
	}
	private static void update() {
		for (Module.Manager t : targets) {
			try {
				t.update();
				t.getController().update();
			} catch (Exception e) {
				CrashLogger.logCrash(new Crash("main", e));
			}
		}
	}
	
	/**
	 * CALL when the robot becomes disabled
	 */
	public static void disabled() {
		for (Module.Manager t : targets) {
			try {
				t.disabled();
				t.getController().idle();
			} catch (Exception e) {
				CrashLogger.logCrash(new Crash("main", e));
			}
		}
	}
}
