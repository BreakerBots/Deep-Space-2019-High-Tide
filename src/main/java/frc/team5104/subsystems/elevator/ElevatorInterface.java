package frc.team5104.subsystems.elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.util.managers.Subsystem;

class ElevatorInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX talon1 = new TalonSRX(Ports.ELEVATOR_TALON_1);
	private TalonSRX talon2 = new TalonSRX(Ports.ELEVATOR_TALON_2);
	
	//Functions
	protected void setMotionMagic(double height) {
		talon1.set(ControlMode.MotionMagic, height / Constants.ELEVATOR_SPOOL_CIRC * 4096.0);
	}
	protected void setPercentOutput(double percent) {
		talon1.set(ControlMode.PercentOutput, percent);
	}
	public void stop() {
		talon1.set(ControlMode.Disabled, 0);
	}
	
	protected double getHeight() {
		return getRawEncoder() / 4096.0 * Constants.ELEVATOR_SPOOL_CIRC;
	}
	protected double getRawEncoder() {
		return talon1.getSelectedSensorPosition();
	}
	protected void resetEncoder() {
		talon1.setSelectedSensorPosition(0);
	}
	protected boolean lowerLimitSwitchHit() {
		return false;//talon1.getSensorCollection().isRevLimitSwitchClosed();
	}
	protected boolean upperLimitSwitchHit() {
		return false;//talon1.getSensorCollection().isFwdLimitSwitchClosed();
	}
	
	
	//Config
	protected void init() {
		talon1.configFactoryDefault();
		talon2.configFactoryDefault();
	}
}
