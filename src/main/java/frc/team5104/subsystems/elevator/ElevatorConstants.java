package frc.team5104.subsystems.elevator;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class ElevatorConstants {
	static enum ElevatorState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum ElevatorPosition { 
		BOTTOM(-2),  
		CARGO_SHIP(28), CARGO_WALL(20), CARGO_L1(4), CARGO_L2(28), CARGO_L3(54), 
		HATCH_L2(24), HATCH_L3(50);
		public double height; private ElevatorPosition(double height) { this.height = height; }
	}
	
	static final double ELEVATOR_SPOOL_CIRC = 1.25 * Math.PI;
	static final double ELEVATOR_CALIBRATE_SPEED = 0.25;
	static final int ELEVATOR_CURRENT_LIMIT = 20;
	static final NeutralMode ELEVATOR_NEUTRAL_MODE = NeutralMode.Brake;
	static final double ELEVATOR_MOTION_KP = 0.5;
	static final double ELEVATOR_MOTION_KI = 0;
	static final double ELEVATOR_MOTION_KD = 6;
	static final int ELEVATOR_MOTION_ACCEL = 20000;
	static final int ELEVATOR_MOTION_CRUISE_VELOCITY = 20000;
	static final double ELEVATOR_HEIGHT_TOL = 6;
	static final double ELEVATOR_HEIGHT_TOL_ROUGH = 12;
}
