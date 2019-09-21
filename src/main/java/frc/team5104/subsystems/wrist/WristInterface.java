package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.subsystems.Subsystem;

class WristInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX wristTalon = new TalonSRX(12);
	
	//Functions
	protected void setMotionMagic(double pos) {
		wristTalon.set(ControlMode.MotionMagic, pos);
	}
	protected void setPercentOutput(double percent) {
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	protected double getRotation() {
		return getRawRotation() / 4096.0 * 360.0;
	}
	protected double getRawRotation() {
		return wristTalon.getSelectedSensorPosition();
	}
	protected void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	protected boolean lowerLimitSwitchHit() {
		return wristTalon.getSensorCollection().isFwdLimitSwitchClosed();
	}
	
	//Config
	protected void init() {
		wristTalon.configFactoryDefault();
		
		wristTalon.config_kP(0, 0);
		wristTalon.config_kI(0, 0);
		wristTalon.config_kD(0, 0);
		wristTalon.config_kF(0, 0);
		wristTalon.configMotionAcceleration(0);
		wristTalon.configMotionCruiseVelocity(0);
	}
}
