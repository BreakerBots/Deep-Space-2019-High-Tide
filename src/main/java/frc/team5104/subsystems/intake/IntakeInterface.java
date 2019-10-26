package frc.team5104.subsystems.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.util.AnalogToDigital;
import frc.team5104.util.managers.Subsystem;

public class IntakeInterface extends Subsystem.Interface {

	//Devices
	private VictorSPX leftVictor = new VictorSPX(Ports.INTAKE_TALON_LEFT);
	private VictorSPX rightVictor = new VictorSPX(Ports.INTAKE_TALON_RIGHT);
	private DoubleSolenoid solenoid = new DoubleSolenoid(Ports.INTAKE_PISTON_FORWARD, Ports.INTAKE_PISTON_REVERSE);
	private AnalogToDigital bannerHatch = new AnalogToDigital(Ports.INTAKE_BANNER_HATCH);
	private AnalogToDigital bannerCargo = new AnalogToDigital(Ports.INTAKE_BANNER_CARGO);
	
	//Functions
	void setWheelSpeed(double percentSpeed) {
		leftVictor.set(ControlMode.PercentOutput, percentSpeed);
	}
	void stopWheels() {
		leftVictor.set(ControlMode.Disabled, 0);
	}
	void setMode(IWEGamePiece targetGameObject) {
		if (targetGameObject == IWEGamePiece.HATCH) solenoid.set(Value.kForward);
		else solenoid.set(Value.kReverse);
	}
	boolean hasHatch() {
		return !bannerHatch.get();
	}
	boolean hasCargo() {
		return !bannerCargo.get();
	}
	
	//Config
	protected void init() {
		leftVictor.configFactoryDefault();
//		leftTalon.configContinuousCurrentLimit(Constants.INTAKE_CURRENT_LIMIT, 10);
//		leftTalon.enableCurrentLimit(true);
		leftVictor.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		
		rightVictor.configFactoryDefault();
//		rightTalon.configContinuousCurrentLimit(Constants.INTAKE_CURRENT_LIMIT, 10);
//		rightTalon.enableCurrentLimit(true);
		rightVictor.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		rightVictor.set(ControlMode.Follower, leftVictor.getDeviceID());
	}
}
