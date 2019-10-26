package frc.team5104.subsystems.elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.util.managers.Subsystem;

class ElevatorInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX talon1 = new TalonSRX(Ports.ELEVATOR_TALON_1);
	private TalonSRX talon2 = new TalonSRX(Ports.ELEVATOR_TALON_2);
	
	//Functions
	void setMotionMagic(double height) {
		talon1.set(
			ControlMode.MotionMagic, height / Constants.ELEVATOR_SPOOL_CIRC * 4096.0,
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	private double getFTerm() {
		//0 -> 0
		//0-48 -> 0.05
		//48+ -> 4
		return 0;
	}
	void setPercentOutput(double percent) {
//		console.log((percent * talon1.getBusVoltage()) + "V, " + talon1.getOutputCurrent() + "A");
		talon1.set(ControlMode.PercentOutput, percent);
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
	boolean lowerLimitSwitchHit() {
		return false;//talon1.getSensorCollection().isRevLimitSwitchClosed();
	}
	boolean upperLimitSwitchHit() {
		return false;//talon1.getSensorCollection().isFwdLimitSwitchClosed();
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
		
		talon2.configFactoryDefault();
		talon2.configContinuousCurrentLimit(Constants.ELEVATOR_CURRENT_LIMIT, 10);
		talon2.enableCurrentLimit(true);
		talon2.setNeutralMode(Constants.ELEVATOR_NEUTRAL_MODE);
		talon2.set(ControlMode.Follower, talon1.getDeviceID());
	}
}
