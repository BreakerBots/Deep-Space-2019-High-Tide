package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.GamePiece;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.AnalogToDigital;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	protected String getName() { return "Intake"; }

	private static VictorSPX leftVictor, rightVictor;
	private static DoubleSolenoid solenoid;
	private static AnalogToDigital bannerHatch, bannerCargo;
	
	//Actions
	private static MovingAverage cargoBuffer = new MovingAverage(10, 0);
	private static MovingAverage hatchBuffer = new MovingAverage(10, 0);
	public static boolean hasHatch() {
		return hatchBuffer.getBooleanOutput();
	}
	public static boolean hasCargo() {
		return cargoBuffer.getBooleanOutput();
	}
	
	//Loop
	protected void update() {
		//Open/Close Intake
		setMode((Superstructure.getGamePiece() == GamePiece.CARGO && Superstructure.getMode() == Mode.INTAKE));
		
		//Set Wheel Speed
		if (Superstructure.getSystemState() == SystemState.DISABLED)
			setWheelSpeed(0);
		else if (Superstructure.getMode() == Mode.IDLE || Superstructure.getMode() == Mode.PLACE) {
			if (Superstructure.getGamePiece() == GamePiece.HATCH ? hasHatch() : hasCargo()) {
				//Hold
				setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
						Constants.INTAKE_HOLD_SPEED_HATCH : 
						-Constants.INTAKE_HOLD_SPEED_CARGO
					);
			}
			else {
				//Idle
				setWheelSpeed(0);
			}
		}
		else if (Superstructure.getMode() == Mode.INTAKE) {
			//Intake
			setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
					Constants.INTAKE_INTAKE_SPEED_HATCH : 
					-Constants.INTAKE_INTAKE_SPEED_CARGO
				);
		}
		else if (Superstructure.getMode() == Mode.EJECT) {
			//Eject
			setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
					-Constants.INTAKE_EJECT_SPEED_HATCH : 
					Constants.INTAKE_EJECT_SPEED_CARGO
				);
		}
		
		//Banner Sensors
		cargoBuffer.update(!bannerHatch.get());
		hatchBuffer.update(!bannerCargo.get());
	}
	
	//Internal Functions
	static void setWheelSpeed(double percentSpeed) {
		leftVictor.set(ControlMode.PercentOutput, percentSpeed);
	}
	static void stopWheels() {
		leftVictor.set(ControlMode.Disabled, 0);
	}
	static void setMode(boolean open) {
		if (open)
			solenoid.set(Value.kReverse);
		else solenoid.set(Value.kForward);
	}
	
	//Config
	protected void init() {
		leftVictor = new VictorSPX(Ports.INTAKE_TALON_LEFT);
		rightVictor = new VictorSPX(Ports.INTAKE_TALON_RIGHT);
		solenoid = new DoubleSolenoid(Ports.INTAKE_PISTON_FORWARD, Ports.INTAKE_PISTON_REVERSE);
		bannerHatch = new AnalogToDigital(Ports.INTAKE_BANNER_HATCH);
		bannerCargo = new AnalogToDigital(Ports.INTAKE_BANNER_CARGO);
		
		leftVictor.configFactoryDefault();
		leftVictor.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		
		rightVictor.configFactoryDefault();
		rightVictor.setNeutralMode(Constants.INTAKE_NEUTRAL_MODE);
		rightVictor.set(ControlMode.Follower, leftVictor.getDeviceID());
	}
	protected void enabled() { stopWheels(); }
	protected void disabled() { stopWheels(); }
}
