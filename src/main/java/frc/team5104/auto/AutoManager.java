/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto;

import edu.wpi.first.wpilibj.Notifier;
import frc.team5104.main.Constants;
import frc.team5104.main.setup.RobotState;
import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;

public class AutoManager {
	private static Notifier _thread = null;
	private static AutoPathScheduler pathScheduler;
	
	private static void init() {
		
		//choose path
		pathScheduler = new AutoPathScheduler(AutoSelector.choosePath());
		
		//loop
		_thread = new Notifier(() -> {try {
			if (RobotState.isSandstorm()) {
				//update path
				pathScheduler.update();
			}
			else {
				//stop
				_thread.stop();
			}
		} catch (Exception e) { CrashLogger.logCrash(new Crash("auto", e)); }});
	}
	
	public static void run() {
		if (_thread == null) init();
		_thread.startPeriodic(1.0 / Constants.ODOMETRY_LOOP_SPEED);
	}
	
	public static void stop() {
		if (_thread != null) _thread.stop();
		_thread = null;
		pathScheduler = null;
	}
}
