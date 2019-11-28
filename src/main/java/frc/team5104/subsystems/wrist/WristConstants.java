package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class WristConstants {
	static enum WristState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum WristPosition {
		BACK(0), HATCH_INTAKE(72), HATCH_EJECT(75), CARGO_EJECT_ROCKET(40), 
		CARGO_EJECT_SHIP(120), CARGO_INTAKE_WALL(50), CARGO_INTAKE_GROUND(125); /*CARGO_INTAKE_GROUND(160), HATCH_PLACE_ANGLED(60), CARGO_PLACE_ANGLED(135)*/
		public double degrees; private WristPosition(double degrees) { this.degrees = degrees; }
	}
	
	public static final double WRIST_CALIBRATE_SPEED = 0.25;
	public static final int WRIST_CURRENT_LIMIT = 20;
	public static final NeutralMode WRIST_NEUTRAL_MODE = NeutralMode.Brake;
	public static final double WRIST_MOTION_KP = 20;
	public static final double WRIST_MOTION_KI = 0;
	public static final double WRIST_MOTION_KD = 200;
	public static final int WRIST_MOTION_ACCEL = 1000;
	public static final int WRIST_MOTION_CRUISE_VELOCITY = 2000;
	public static final double WRIST_LIMP_MODE_MAX_SPEED = 0.1;
	public static final int WRIST_LIMP_MODE_TIME_START = 1500;
	public static final double WRIST_ANGLE_TOL = 10;
}
