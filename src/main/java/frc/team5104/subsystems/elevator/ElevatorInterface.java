package frc.team5104.subsystems.elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.subsystems.canifier.CANifier;
import frc.team5104.subsystems.elevator.ElevatorLooper.ElevatorPosition;
import frc.team5104.util.managers.Subsystem;

public class ElevatorInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX talon1 = new TalonSRX(Ports.ELEVATOR_TALON_1);
	private TalonSRX talon2 = new TalonSRX(Ports.ELEVATOR_TALON_2);
	double lastTargetHeight = 0;
	
	//Functions
	void setMotionMagic(double height) {
		lastTargetHeight = height;
		talon1.set(
			ControlMode.MotionMagic, height / Constants.ELEVATOR_SPOOL_CIRC * 4096.0,
			DemandType.ArbitraryFeedForward, getFTerm()
		);
		updateLimitSwitches();
	}
	double getFTerm() {
		double f = 0;
		if (getEncoderHeight() > 26) 
			f += 0.2;
		if (Elevator._looper.elevatorPosition == ElevatorPosition.BOTTOM)
			f -= 0.2;
		return f;
	}
	void setPercentOutput(double percent) {
//		console.log((percent * talon1.getBusVoltage()) + "V, " + talon1.getOutputCurrent() + "A");
		talon1.set(ControlMode.PercentOutput, percent);
		updateLimitSwitches();
	}
	void updateLimitSwitches() {
		talon1.configPeakOutputReverse(CANifier.elevatorLowerLimitHit() ? 0 : -1);
		talon2.configPeakOutputReverse(CANifier.elevatorLowerLimitHit() ? 0 : -1);
	}
	void stop() {
		talon1.set(ControlMode.Disabled, 0);
	}
	double getEncoderHeight() {
		return getRawEncoderPosition() / 4096.0 * Constants.ELEVATOR_SPOOL_CIRC;
	}
	double getRawEncoderVelocity() {
		return talon1.getSelectedSensorVelocity();
	}
	double getRawEncoderPosition() {
		return talon1.getSelectedSensorPosition();
	}
	void resetEncoder() {
		talon1.setSelectedSensorPosition(0);
	}
	boolean encoderDisconnected() {
		return talon1.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	double getMotorPercentOutput() {
		return talon1.getMotorOutputPercent();
	}
	
	//Config
	protected void init() {
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
}
