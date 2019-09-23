/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.util.Units;

public class Constants {
	//Console
	public static final boolean OVERWRITE_NON_MATCH_LOGS = true;
	public static final boolean OVERWRITE_MATCH_LOGS = false;
	
	//Loops
	public static final int MAIN_LOOP_SPEED = 50;
	public static final int ODOMETRY_LOOP_SPEED = 100;
	
	//Robot Meta
	public static final double ROBOT_LENGTH = Units.inchesToFeet(32.0 + 4.0);
	public static final double ROBOT_WIDTH = Units.inchesToFeet(28.0 + 4.0);
	public static final String ROBOT_NAME = "High-Tide";
	
	//IWE (Intake, Wrist, Elevator)
	public static final short IWE_EJECT_TIME = 1000;
	public static final IWEControl IWE_DEFAULT_CONTROL = IWEControl.MANUAL;
	public static final double WRIST_CALIBRATE_SPEED = 0.1;
	public static final double INTAKE_INTAKE_SPEED_HATCH = 0.8;
	public static final double INTAKE_EJECT_SPEED_HATCH = 1;
	public static final double INTAKE_HOLD_SPEED_HATCH = 0.1;
	public static final double INTAKE_INTAKE_SPEED_CARGO = 0.8;
	public static final double INTAKE_EJECT_SPEED_CARGO = 1;
	public static final double INTAKE_HOLD_SPEED_CARGO = 0.1;
	public static final double ELEVATOR_SPOOL_CIRC = 1.25 * Math.PI;
	public static final double ELEVATOR_CALIBRATE_SPEED = 0.1;
}