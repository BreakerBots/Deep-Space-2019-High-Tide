package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
	
	protected double getRotation() {
		return getRawRotation() / 4096.0 * 360.0;
	}
	protected double getRawRotation() {
		return wristTalon.getSelectedSensorPosition();
	}
	protected void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	protected boolean backLimitSwitchHit() {
		return false;//wristTalon.getSensorCollection().isFwdLimitSwitchClosed();
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
