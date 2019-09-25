package frc.team5104.subsystems.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.util.managers.Subsystem;

public class IntakeInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX leftTalon = new TalonSRX(Ports.INTAKE_TALON_LEFT);
	private TalonSRX rightTalon = new TalonSRX(Ports.INTAKE_TALON_RIGHT);
	private DoubleSolenoid solenoid = new DoubleSolenoid(Ports.INTAKE_PISTON_FORWARD, Ports.INTAKE_PISTON_REVERSE);
	
	//Functions
	public void setWheelSpeed(double percentSpeed) {
		leftTalon.set(ControlMode.PercentOutput, percentSpeed);
	}
	public void stopWheels() {
		leftTalon.set(ControlMode.Disabled, 0);
	}
	public void setMode(IWEGamePiece targetGameObject) {
		if (targetGameObject == IWEGamePiece.HATCH) solenoid.set(Value.kForward);
		else solenoid.set(Value.kReverse);
	}
	public boolean hasHatch() {
		return leftTalon.getSensorCollection().isFwdLimitSwitchClosed();
	}
	public boolean hasCargo() {
		return rightTalon.getSensorCollection().isFwdLimitSwitchClosed();
	}
	
	//Config
	protected void init() {
		leftTalon.configFactoryDefault();
		leftTalon.configContinuousCurrentLimit(Constants.INTAKE_CURRENT_LIMIT, 10);
		leftTalon.enableCurrentLimit(true);
		leftTalon.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		
		rightTalon.configFactoryDefault();
		rightTalon.configContinuousCurrentLimit(Constants.INTAKE_CURRENT_LIMIT, 10);
		rightTalon.enableCurrentLimit(true);
		rightTalon.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		rightTalon.set(ControlMode.Follower, leftTalon.getDeviceID());
	}
}
