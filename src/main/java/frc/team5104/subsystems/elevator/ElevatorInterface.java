package frc.team5104.subsystems.elevator;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.team5104.main.Ports;
import frc.team5104.subsystems.elevator.ElevatorConstants.ElevatorPosition;

public class ElevatorInterface {

	//Devices
	private static TalonSRX talon1, talon2;
	private static CANifier canifier;
	private static double lastTargetHeight;
	
	//Subsystem Internal Functions
	static void setMotionMagic(double height) {
		lastTargetHeight = height;
		talon1.set(
			ControlMode.MotionMagic, height / ElevatorConstants.ELEVATOR_SPOOL_CIRC * 4096.0,
			DemandType.ArbitraryFeedForward, getFTerm()
		);
		updateLimitSwitches();
	}
	static double getFTerm() {
		double f = 0;
		if (getEncoderHeight() > 26) 
			f += 0.08;
		if (Elevator.elevatorPosition == ElevatorPosition.BOTTOM)
			f -= 0.15;
		return f;
	}
	static void setPercentOutput(double percent) {
		talon1.set(ControlMode.PercentOutput, percent);
		updateLimitSwitches();
	}
	static void updateLimitSwitches() {
		if (lowerLimitHit()) {
			talon1.configPeakOutputReverse(0);
			talon2.configPeakOutputReverse(0);
		}
		else if (Elevator.elevatorPosition == ElevatorPosition.BOTTOM && getEncoderHeight() < 6) {
			talon1.configPeakOutputReverse(-0.3);
			talon2.configPeakOutputReverse(-0.3);
		}
		else {
			talon1.configPeakOutputReverse(-1);
			talon2.configPeakOutputReverse(-1);
		}
	}
	static void stop() {
		talon1.set(ControlMode.Disabled, 0);
	}
	static void resetEncoder() {
		talon1.setSelectedSensorPosition(0);
	}
	
	//Subsystem External Functions
	public static double getEncoderHeight() {
		return getRawEncoderPosition() / 4096.0 * ElevatorConstants.ELEVATOR_SPOOL_CIRC;
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
		return Math.abs(getEncoderHeight() - lastTargetHeight) < ElevatorConstants.ELEVATOR_HEIGHT_TOL_ROUGH; 
	}
	
	//Config
	static void init() {
		talon1 = new TalonSRX(Ports.ELEVATOR_TALON_1);
		talon2 = new TalonSRX(Ports.ELEVATOR_TALON_2);
		canifier = new CANifier(Ports.ELEVATOR_CANIFIER);
		
		talon1.configFactoryDefault();
		talon1.configContinuousCurrentLimit(ElevatorConstants.ELEVATOR_CURRENT_LIMIT, 10);
		talon1.enableCurrentLimit(true);
		talon1.setNeutralMode(ElevatorConstants.ELEVATOR_NEUTRAL_MODE);
		talon1.config_kP(0, ElevatorConstants.ELEVATOR_MOTION_KP);
		talon1.config_kI(0, ElevatorConstants.ELEVATOR_MOTION_KI);
		talon1.config_kD(0, ElevatorConstants.ELEVATOR_MOTION_KD);
		talon1.config_kF(0, 0);
		talon1.configMotionAcceleration(ElevatorConstants.ELEVATOR_MOTION_ACCEL);
		talon1.configMotionCruiseVelocity(ElevatorConstants.ELEVATOR_MOTION_CRUISE_VELOCITY);
		talon1.setSensorPhase(true);
		talon1.configClosedloopRamp(0.1);
		
		talon2.configFactoryDefault();
		talon2.configContinuousCurrentLimit(ElevatorConstants.ELEVATOR_CURRENT_LIMIT, 10);
		talon2.enableCurrentLimit(true);
		talon2.setNeutralMode(ElevatorConstants.ELEVATOR_NEUTRAL_MODE);
		talon2.set(ControlMode.Follower, talon1.getDeviceID());
		talon2.configClosedloopRamp(0.1);
	}
}
