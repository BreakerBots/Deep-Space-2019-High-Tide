/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto;

import java.util.ArrayList;
import frc.team5104.auto.util.BreakerPath;

/**
 * Handles the Execution of BreakerCommands inside the assigned BreakerCommandGroup (Entire Path)
 */
public class BreakerPathScheduler {
	public static BreakerPath path = null;
	public static int pathActionsLength = 0;
	public static ArrayList<Integer> pathIndex = new ArrayList<Integer>();
	public static boolean pathActionInitialized = false;
	
	/** Set the target path */
	public static void set(BreakerPath targetPath) {
		//Save the new Command Group
		path = targetPath;
		pathActionsLength = path.pathActionsLength;
		
		//Reset Command Group Filter Index
		pathIndex = 0;
		
		//Say that the first command hasn't been Initiated
		pathActionInitialized = false;
	}
	
	/** Update the current path */
	public static void update() {
		if (pathIndex < path.pathActionsLength) {
			if (!pathActionInitialized) {
				path.pathActions[pathIndex].init();
				pathActionInitialized = true;
			}
			
			if (path.pathActions[pathIndex].update()) {
				path.pathActions[pathIndex].end();
				
				pathIndex++;
				pathActionInitialized = false;
			}
		}
	}
}
