package frc.team5104.subsystems.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class IntakeConstants {
	public static final double INTAKE_INTAKE_SPEED_HATCH = 1;
	public static final double INTAKE_EJECT_SPEED_HATCH = 1;
	public static final double INTAKE_HOLD_SPEED_HATCH = 0.05;
	public static final double INTAKE_INTAKE_SPEED_CARGO = 1;
	public static final double INTAKE_EJECT_SPEED_CARGO = 1;
	public static final double INTAKE_HOLD_SPEED_CARGO = 0.05;
	public static final int INTAKE_CURRENT_LIMIT = 10;
	public static final NeutralMode INTAKE_NEUTRAL_MODE = NeutralMode.Coast;
	public static final int INTAKE_EJECT_TIME = 250;
}
