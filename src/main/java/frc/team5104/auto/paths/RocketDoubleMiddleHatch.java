/*BreakerBots Robotics Team 2019*/
package frc.team5104.auto.paths;

import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.util.AutoPath;
import frc.team5104.auto.util.TrajectoryWaypoint;

//!! NEEDS ACTUAL PATH MEASUREMENTS !!
public class RocketDoubleMiddleHatch extends AutoPath {
	public RocketDoubleMiddleHatch() {
		//Drive Backwards then Place Hatch
		//add(new IWEPlace(IWEHeight.L2));
		add(new DriveTrajectoryAction(new TrajectoryWaypoint[] {
				new TrajectoryWaypoint(0, 0, 0),
				new TrajectoryWaypoint(5, -5, 0)
		}));
		//add(new IWEEject());
		
		//"Bump Out"
		add(new DriveTrajectoryAction(new TrajectoryWaypoint[] {
				new TrajectoryWaypoint(0, 0, 0),
				new TrajectoryWaypoint(-1, -1, 0)
		}));
		
		//Intake Hatch
		//add(new IWEIntake());
		add(new DriveTrajectoryAction(new TrajectoryWaypoint[] {
				new TrajectoryWaypoint(0, 0, 0),
				new TrajectoryWaypoint(5, 5, 0)
		}));
		
		//Backout
		add(new DriveTrajectoryAction(new TrajectoryWaypoint[] {
				new TrajectoryWaypoint(0, 0, 0),
				new TrajectoryWaypoint(-1, -5, 0)
		}));
		
		//Place Hatch
		//add(new IWEPlace(IWEHeight.L2));
		add(new DriveTrajectoryAction(new TrajectoryWaypoint[] {
				new TrajectoryWaypoint(0, 0, 0),
				new TrajectoryWaypoint(5, 5, 0)
		}));
		//add(new IWEEject());
	}
}
