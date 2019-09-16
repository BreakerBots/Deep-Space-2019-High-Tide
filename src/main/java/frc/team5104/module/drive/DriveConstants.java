/*BreakerBots Robotics Team 2019*/
package frc.team5104.module.drive;

import frc.team5104.module.Module;

public class DriveConstants extends Module.Constants {
	//Speed Adjustments
	protected static final double WHEELACCOUNT_RIGHT_FORWARD = 1.000; //(TalSpeed) [Measure] Multiple the right motor by (For Driving Straight)
	protected static final double WHEELACCOUNT_RIGHT_REVERSE = 1.000; //(TalSpeed) [Measure] Multiple the right motor by (For Driving Straight)
	protected static final double WHEELACCOUNT_LEFT_FORWARD  = 1.000; //(TalSpeed) [Measure] Multiple the left  motor by (For Driving Straight)
	protected static final double WHEELACCOUNT_LEFT_REVERSE  = 1.000; //(TalSpeed) [Measure] Multiple the left  motor by (For Driving Straight)
	protected static final double MINSPEED_FORWARD = 1.75;//0.08;
	protected static final double MINSPEED_TURN = 3.3;//0.12;
	
	//Current Limiting
	public static final int CURRENT_LIMIT = 30;
}
