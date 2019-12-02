package frc.team5104.subsystems;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.GamePiece;
import frc.team5104.Superstructure.Height;
import frc.team5104.Superstructure.IntakeMode;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Elevator extends Subsystem {
	protected String getName() { return "Elevator"; }

	private static TalonSRX talon1, talon2;
	private static CANifier canifier;
	private static double lastTargetHeight;
	private static MovingAverage limitSwitchZeroBuffer = new MovingAverage(5, false);
	public static double desiredElevatorManaul = 0;

	//Loop
	protected void update() {
		//Auto
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS)
			setMotionMagic(getTargetHeight());
		
		//Calibrating
		else if (Superstructure.getSystemState() == SystemState.CALIBRATING && !lowerLimitHit())
			setPercentOutput(-Constants.ELEVATOR_CALIBRATE_SPEED);
		
		//Manual
		else if (Superstructure.getSystemState() == SystemState.MANUAL)
			setPercentOutput(desiredElevatorManaul);
		
		//Stop
		else stop();
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(lowerLimitHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			resetEncoder();
	}

	//Subsystem Internal Functions
	private static void setMotionMagic(double height) {
		lastTargetHeight = height;
		double ff = 0;
		if (getEncoderHeight() > 26) 
			ff += 0.08;
		talon1.set(
			ControlMode.MotionMagic, height / Constants.ELEVATOR_SPOOL_CIRC * 4096.0,
			DemandType.ArbitraryFeedForward, ff
		);
		updateLimitSwitches();
	}
	private static void setPercentOutput(double percent) {
		talon1.set(ControlMode.PercentOutput, percent);
		updateLimitSwitches();
	}
	private static void updateLimitSwitches() {
		if (lowerLimitHit()) {
			talon1.configPeakOutputReverse(0);
			talon2.configPeakOutputReverse(0);
		}
		else {
			talon1.configPeakOutputReverse(-1);
			talon2.configPeakOutputReverse(-1);
		}
	}
	private static void stop() {
		talon1.set(ControlMode.Disabled, 0);
	}
	private static double getTargetHeight() {
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS) {
			if (Superstructure.getGamePiece() == GamePiece.CARGO && Superstructure.getMode() == Mode.INTAKE && Superstructure.getIntakeMode() == IntakeMode.WALL)
				return Constants.ELEVATOR_HEIGHT_CARGO_WALL;
			if (Superstructure.getMode() == Mode.INTAKE || Superstructure.getMode() == Mode.IDLE)
				return 0;
			if (Superstructure.getGamePiece() == GamePiece.CARGO) {
				if (Superstructure.getHeight() == Height.L1)
					return Constants.ELEVATOR_HEIGHT_CARGO_L1;
				else if (Superstructure.getHeight() == Height.L2)
					return Constants.ELEVATOR_HEIGHT_CARGO_L2;
				else if (Superstructure.getHeight() == Height.L3)
					return Constants.ELEVATOR_HEIGHT_CARGO_L3;
				else if (Superstructure.getHeight() == Height.SHIP)
					return Constants.ELEVATOR_HEIGHT_CARGO_SHIP;
			}
			if (Superstructure.getHeight() == Height.L2)
				return Constants.ELEVATOR_HEIGHT_HATCH_L2;
			if (Superstructure.getHeight() == Height.L3)
				return Constants.ELEVATOR_HEIGHT_HATCH_L3;
		}
		return 0;
	}
	
	//Subsystem External Functions
	public static void resetEncoder() {
		talon1.setSelectedSensorPosition(0);
	}
	public static double getEncoderHeight() {
		return getRawEncoderPosition() / 4096.0 * Constants.ELEVATOR_SPOOL_CIRC;
	}
	public static double getRawEncoderVelocity() {
		return talon1.getSelectedSensorVelocity();
	}
	public static double getRawEncoderPosition() {
		return talon1.getSelectedSensorPosition();
	}
	public static boolean encoderDisconnected() {
		return talon1.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	public static double getMotorPercentOutput() {
		return talon1.getMotorOutputPercent();
	}
	public static boolean lowerLimitHit() {
		return !canifier.getGeneralInput(CANifier.GeneralPin.LIMR);
	}
	public static boolean isRoughlyAtTargetHeight() { 
		return Math.abs(getEncoderHeight() - lastTargetHeight) < Constants.ELEVATOR_HEIGHT_TOL_ROUGH; 
	}
	
	//Config
	protected void init() {
		talon1 = new TalonSRX(Ports.ELEVATOR_TALON_1);
		talon2 = new TalonSRX(Ports.ELEVATOR_TALON_2);
		canifier = new CANifier(Ports.ELEVATOR_CANIFIER);
		
		talon1.configFactoryDefault();
		talon1.configContinuousCurrentLimit(Constants.ELEVATOR_CURRENT_LIMIT, 10);
		talon1.enableCurrentLimit(true);
		talon1.setNeutralMode(Constants.ELEVATOR_NEUTRAL_MODE);
		talon1.config_kP(0, Constants.ELEVATOR_MOTION_KP);
		talon1.config_kI(0, Constants.ELEVATOR_MOTION_KI);
		talon1.config_kD(0, Constants.ELEVATOR_MOTION_KD);
		talon1.config_kF(0, 0);
		talon1.configMotionAcceleration(Constants.ELEVATOR_MOTION_ACCEL);
		talon1.configMotionCruiseVelocity(Constants.ELEVATOR_MOTION_CRUISE_VELOCITY);
		talon1.setSensorPhase(true);
		talon1.configClosedloopRamp(0.1);
		
		talon2.configFactoryDefault();
		talon2.configContinuousCurrentLimit(Constants.ELEVATOR_CURRENT_LIMIT, 10);
		talon2.enableCurrentLimit(true);
		talon2.setNeutralMode(Constants.ELEVATOR_NEUTRAL_MODE);
		talon2.set(ControlMode.Follower, talon1.getDeviceID());
		talon2.configClosedloopRamp(0.1);
	}
	protected void enabled() { stop(); }
	protected void disabled() { stop(); }
}