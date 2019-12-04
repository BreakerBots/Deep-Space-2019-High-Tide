/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.auto.paths;

import frc.team5104.auto.actions.DriveStopAction;
import frc.team5104.auto.actions.DriveTrajectoryAction;
import frc.team5104.auto.util.AutoPath;
import frc.team5104.auto.util.Position;

public class ExamplePath extends AutoPath {
	public ExamplePath() {
		add(new DriveTrajectoryAction(new Position[] {
				new Position(0, 0, 0, true),
				new Position(-5, -5, 0, true)
		}));
		add(new DriveStopAction());
	}
}
