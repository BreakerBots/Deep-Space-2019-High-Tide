package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.util.managers.Subsystem;

class WristInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX wristTalon = new TalonSRX(Ports.WRIST_TALON);
	
	//Functions
	protected void setMotionMagic(double degrees) {
		wristTalon.set(ControlMode.MotionMagic, degrees / 360.0 * 4096.0);
	}
	protected void setPercentOutput(double percent) {
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	public void stop() {
		wristTalon.set(ControlMode.Disabled, 0);
	}
	
	protected double getEncoderRotation() {
		return getRawEncoderRotation() / 4096.0 * 360.0;
	}
	protected double getRawEncoderRotation() {
		return wristTalon.getSelectedSensorPosition();
	}
	protected double getRawEncoderVelocity() {
		return wristTalon.getSelectedSensorVelocity();
	}
	protected void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	public boolean encoderDisconnected() {
		return wristTalon.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	
	protected boolean backLimitSwitchHit() {
		return false;//wristTalon.getSensorCollection().isFwdLimitSwitchClosed();
	}
	
	
	//Config
	protected void init() {
		wristTalon.configFactoryDefault();
		wristTalon.configContinuousCurrentLimit(Constants.WRIST_CURRENT_LIMIT, 10);
		wristTalon.enableCurrentLimit(true);
		wristTalon.setNeutralMode(Constants.WRIST_NEUTRAL_MODE);
		wristTalon.config_kP(0, Constants.WRIST_KP);
		wristTalon.config_kI(0, Constants.WRIST_KI);
		wristTalon.config_kD(0, Constants.WRIST_KD);
		wristTalon.config_kF(0, Constants.WRIST_KF);
		wristTalon.configMotionAcceleration(Constants.WRIST_MOTION_ACCEL);
		wristTalon.configMotionCruiseVelocity(Constants.WRIST_MOTION_CRUISE_VELOCITY);
	}
}
