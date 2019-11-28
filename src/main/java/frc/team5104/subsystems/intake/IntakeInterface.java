package frc.team5104.subsystems.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.main.Ports;
import frc.team5104.util.AnalogToDigital;

public class IntakeInterface {

	//Devices
	private static VictorSPX leftVictor, rightVictor;
	private static DoubleSolenoid solenoid;
	private static AnalogToDigital bannerHatch, bannerCargo;
	static enum IntakePistonMode { OPEN, CLOSED };
	
	//Internal Functions
	static void setWheelSpeed(double percentSpeed) {
		leftVictor.set(ControlMode.PercentOutput, percentSpeed);
	}
	static void stopWheels() {
		leftVictor.set(ControlMode.Disabled, 0);
	}
	static void setMode(IntakePistonMode pistonMode) {
		if (pistonMode == IntakePistonMode.CLOSED) solenoid.set(Value.kForward);
		else solenoid.set(Value.kReverse);
	}
	static boolean hasHatch() {
		return !bannerHatch.get();
	}
	static boolean hasCargo() {
		return !bannerCargo.get();
	}
	
	//Config
	static void init() {
		leftVictor = new VictorSPX(Ports.INTAKE_TALON_LEFT);
		rightVictor = new VictorSPX(Ports.INTAKE_TALON_RIGHT);
		solenoid = new DoubleSolenoid(Ports.INTAKE_PISTON_FORWARD, Ports.INTAKE_PISTON_REVERSE);
		bannerHatch = new AnalogToDigital(Ports.INTAKE_BANNER_HATCH);
		bannerCargo = new AnalogToDigital(Ports.INTAKE_BANNER_CARGO);
		
		leftVictor.configFactoryDefault();
		leftVictor.setNeutralMode(IntakeConstants.INTAKE_NEUTRAL_MODE);
		
		rightVictor.configFactoryDefault();
		rightVictor.setNeutralMode(IntakeConstants.INTAKE_NEUTRAL_MODE);
		rightVictor.set(ControlMode.Follower, leftVictor.getDeviceID());
	}
}
